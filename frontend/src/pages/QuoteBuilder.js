import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Select, { components } from 'react-select';
import api from '../api/axiosConfig';

const TRAILER_MODELS = [
    { value: 'DCS', label: 'DCS' },
    { value: 'DDS', label: 'DDS' },
    { value: 'IWS', label: 'IWS' },
    { value: 'SDS', label: 'SDS' },
    { value: 'SFS', label: 'SFS' },
    { value: 'TRASH', label: 'TRASH' },
];


const CustomOption = (props) => {
    return (
        <components.Option {...props}>
            <div className="flex flex-col border-b border-gray-100 pb-2 last:border-0 cursor-pointer">
                <div className="flex justify-between items-center mb-1">
                    <span className="font-bold text-gray-800 text-sm">{props.data.assembly.name}</span>
                    <span className="text-blue-600 font-bold text-xs">
                        ${props.data.assembly.defaultPrice?.toFixed(2) ?? '0.00'}
                    </span>
                </div>
                <div className="text-xs text-gray-500 italic line-clamp-2 leading-relaxed">
                    {props.data.assembly.description || 'No description available'}
                </div>
            </div>
        </components.Option>
    );
};

function QuoteBuilder() {
    const [applySurcharge, setApplySurcharge] = useState(false);
    const navigate = useNavigate();
    const [assemblies, setAssemblies] = useState([]);
    const [filteredAssemblies, setFilteredAssemblies] = useState([]);
    const [selectedModel, setSelectedModel] = useState(null);
    const [quoteLines, setQuoteLines] = useState([]);
    const [quote, setQuote] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [customers, setCustomers] = useState([]);
    const [selectedCustomer, setSelectedCustomer] = useState('');
    const [customerSearch, setCustomerSearch] = useState('');

    useEffect(() => {
        fetchAssemblies();
        fetchCustomers();
    }, []);

    // Filter assemblies whenever the selected model changes
// Filter assemblies based on the exact prefix of the Part Number
    useEffect(() => {
        if (!selectedModel) {
            // If no trailer model is selected, show an empty list
            // This keeps the UI clean until they pick SDS, DCS, etc.
            setFilteredAssemblies([]);
        } else {
            const filtered = assemblies.filter(a => {
                const itemName = a.name || "";
                // This checks if the QuickBooks Item Name starts with DCS, SDS, etc.
                // .toUpperCase() ensures it's not case-sensitive
                return itemName.toUpperCase().startsWith(selectedModel.value.toUpperCase());
            });
            setFilteredAssemblies(filtered);
        }
    }, [selectedModel, assemblies]);

    const fetchCustomers = async () => {
        try {
            const response = await api.get('/api/customers');
            setCustomers(response.data);
        } catch (err) { setError('Failed to load customers'); }
    };

    const searchCustomers = async (name) => {
        setCustomerSearch(name);
        try {
            const url = name ? `/api/customers/search?name=${name}` : '/api/customers';
            const response = await api.get(url);
            setCustomers(response.data);
        } catch (err) { setError('Failed to search customers'); }
    };

    const fetchAssemblies = async () => {
        try {
            const response = await api.get('/api/assemblies');
            setAssemblies(response.data);
            setFilteredAssemblies(response.data);
        } catch (err) { setError('Failed to load assemblies'); }
    };

    const createQuote = async (customerId) => {
        try {
            const response = await api.post('/api/quotes', {
                customer: { id: customerId }
            });
            return response.data;
        } catch (err) { setError('Failed to create quote'); return null; }
    };

    const addToQuote = async (assembly) => {
        if (!selectedCustomer) {
            setError('Please select a customer before adding items');
            return;
        }
        setLoading(true);
        try {
            let currentQuote = quote || await createQuote(selectedCustomer);
            if (!currentQuote) return;
            setQuote(currentQuote);

            await api.post(`/api/quotes/${currentQuote.id}/lines`, {
                assembly: { id: assembly.id },
                quantity: 1
            });

            const existing = quoteLines.find(l => l.assembly.id === assembly.id);
            if (existing) {
                setQuoteLines(quoteLines.map(l =>
                    l.assembly.id === assembly.id
                        ? { ...l, quantity: l.quantity + 1, lineTotal: l.unitPrice * (l.quantity + 1) }
                        : l
                ));
            } else {
                setQuoteLines([...quoteLines, {
                    assembly,
                    quantity: 1,
                    unitPrice: assembly.defaultPrice,
                    lineTotal: assembly.defaultPrice
                }]);
            }
            setError('');
        } catch (err) { setError('Failed to add item'); }
        setLoading(false);
    };

    const removeLine = (assemblyId) => {
        setQuoteLines(quoteLines.filter(l => l.assembly.id !== assemblyId));
    };

    const getTotal = () => {
        const subtotal = quoteLines.reduce((sum, line) => sum + (line.lineTotal || 0), 0);
        return applySurcharge ? subtotal * 1.12 : subtotal;
    };

    const saveQuote = () => {
        console.log('quote:', quote);
        console.log('selectedCustomer:', selectedCustomer);
        if (!quote || !selectedCustomer) {
            setError('Missing quote or customer - quote: ' + JSON.stringify(quote) + ' customer: ' + selectedCustomer);
            return;
        }
        navigate('/dashboard');
    };

    const assemblyOptions = filteredAssemblies.map(a => ({
        value: a.id,
        label: a.name,
        assembly: a
    }));

    const selectedCustomerName = customers.find(c => c.id === selectedCustomer)?.name;

    return (
        <div className="min-h-screen bg-gray-100 pb-12">
            <nav className="bg-white shadow px-6 py-4 flex justify-between items-center mb-6">
                <h1 className="text-xl font-bold text-gray-800">Innovative Quote Builder</h1>
                <button onClick={() => navigate('/dashboard')} className="text-sm text-gray-600 hover:text-gray-800 font-medium">
                    ← Back to Dashboard
                </button>
            </nav>

            <div className="max-w-6xl mx-auto px-6">
                {error && <div className="bg-red-100 text-red-700 px-4 py-2 rounded mb-6 shadow-sm">{error}</div>}

                {/* STEP 1: Customer Selection */}
                <div className="bg-white rounded-lg shadow p-6 mb-6">
                    <h2 className="text-lg font-semibold text-gray-700 mb-4">1. Select Customer</h2>
                    <div className="flex flex-col md:flex-row gap-4">
                        <input
                            type="text"
                            value={customerSearch}
                            onChange={(e) => searchCustomers(e.target.value)}
                            placeholder="Type to search customers..."
                            className="flex-1 border border-gray-300 rounded px-3 py-2 focus:ring-2 focus:ring-blue-500 focus:outline-none"
                        />
                        <select
                            value={selectedCustomer}
                            onChange={(e) => setSelectedCustomer(e.target.value)}
                            className="flex-1 border border-gray-300 rounded px-3 py-2 bg-white"
                        >
                            <option value="">-- Select from results --</option>
                            {customers.map(c => (
                                <option key={c.id} value={c.id}>{c.name} {c.phone ? `(${c.phone})` : ''}</option>
                            ))}
                        </select>
                    </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                    {/* Left: Filtered Selection */}
                    <div className="bg-white rounded-lg shadow p-6">
                        <h2 className="text-lg font-semibold text-gray-700 mb-6 border-b pb-2">2. Build Your Trailer</h2>

                        {/* Trailer Model Dropdown */}
                        <div className="mb-6">
                            <label className="block text-sm font-bold text-gray-600 mb-2 uppercase tracking-wide">
                                Step A: Select Trailer Model
                            </label>
                            <Select
                                options={TRAILER_MODELS}
                                value={selectedModel}
                                onChange={setSelectedModel}
                                placeholder="Choose SDS, SFS, DCS, etc..."
                                isClearable
                                className="text-sm"
                            />
                        </div>

                        {/* Filtered Assembly Dropdown */}
                        <div className="mb-4">
                            <label className="block text-sm font-bold text-gray-600 mb-2 uppercase tracking-wide">
                                Step B: Select Specific Assembly
                            </label>
                            <Select
                                key={`${selectedModel?.value}-${quoteLines.length}`}
                                options={assemblyOptions}
                                components={{ Option: CustomOption }}
                                onChange={(opt) => opt && addToQuote(opt.assembly)}
                                placeholder={selectedModel ? `Search ${selectedModel.value} inventory...` : "Select a model first"}
                                isSearchable
                                isDisabled={!selectedModel}
                                className="text-sm"
                            />
                        </div>

                        <div className="mt-8 p-4 bg-blue-50 rounded-lg border border-blue-100">
                            <p className="text-xs text-blue-600 leading-normal italic">
                                * Filtering by Model ensures you only see compatible packages and trailers.
                            </p>
                        </div>
                    </div>

                    {/* Right: Quote Summary */}
                    <div className="bg-white rounded-lg shadow p-6 flex flex-col">
                        <h2 className="text-lg font-semibold text-gray-700 mb-4 border-b pb-2">3. Quote Summary</h2>
                        <div className="flex-1 overflow-y-auto max-h-[400px] mb-6 pr-2">
                            {quoteLines.length === 0 ? (
                                <div className="text-center py-12 border-2 border-dashed border-gray-100 rounded-xl text-gray-400">
                                    No items added.
                                </div>
                            ) : (
                                <div className="space-y-3">
                                    {quoteLines.map((line, index) => (
                                        <div key={index} className="bg-gray-50 rounded-lg p-3 relative">
                                            <button onClick={() => removeLine(line.assembly.id)} className="absolute top-2 right-2 text-gray-400 hover:text-red-500">✕</button>
                                            <p className="font-bold text-gray-800 text-sm">{line.assembly.name}</p>
                                            <div className="flex justify-between items-center mt-2">
                                                <span className="text-xs text-gray-500">Qty: {line.quantity}</span>
                                                <span className="text-sm font-bold text-gray-900">${line.lineTotal?.toFixed(2)}</span>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            )}
                        </div>

                        {/* Surcharge Toggle */}
                        <div className="flex items-center justify-between py-3 px-4 bg-yellow-50 rounded-lg border border-yellow-100 mb-4">
                            <div className="flex items-center">
                                <input
                                    id="surcharge-checkbox"
                                    type="checkbox"
                                    checked={applySurcharge}
                                    onChange={(e) => setApplySurcharge(e.target.checked)}
                                    className="h-5 w-5 text-blue-600 rounded border-gray-300 focus:ring-blue-500 cursor-pointer"
                                />
                                <label htmlFor="surcharge-checkbox" className="ml-3 text-sm font-bold text-yellow-800 cursor-pointer">
                                    Apply FET Surcharge
                                </label>
                            </div>
                            {applySurcharge && (
                                <span className="text-xs font-black text-yellow-600 tracking-tighter">
            +${(quoteLines.reduce((sum, line) => sum + (line.lineTotal || 0), 0) * 0.12).toFixed(2)}
        </span>
                            )}
                        </div>

                        <div className="border-t border-gray-100 pt-5">
                            <div className="flex justify-between items-center mb-6">
                                <span className="text-gray-500 font-medium">Grand Total</span>
                                <span className="text-2xl font-black text-blue-600">${getTotal().toFixed(2)}</span>
                            </div>
                            <button
                                onClick={saveQuote}
                                disabled={quoteLines.length === 0 || !selectedCustomer}
                                className="w-full bg-green-600 text-white py-3 rounded-lg hover:bg-green-700 font-bold shadow-md disabled:opacity-50"
                            >
                                Save Quote
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default QuoteBuilder;