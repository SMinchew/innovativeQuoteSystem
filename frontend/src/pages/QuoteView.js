import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import api from '../api/axiosConfig';

function QuoteView() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [quote, setQuote] = useState(null);
    const [lines, setLines] = useState([]);
    const [total, setTotal] = useState(0);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const token = localStorage.getItem('token');

    useEffect(() => {
        fetchQuote();
    }, []);

    const fetchQuote = async () => {
        try {
            console.log('Fetching quote ID:', id);
            console.log('Token:', localStorage.getItem('token'));
            const response = await api.get(`/api/quotes/${id}`);
            setQuote(response.data);
            setLines(response.data.lines || []);

            const totalResponse = await api.get(`/api/quotes/${id}/total`);
            setTotal(totalResponse.data);
        } catch (err) {
            console.error('Quote fetch error:', err.response?.status, err.response?.data);
            setError('Failed to load quote');
        }
        setLoading(false);
    };

    const downloadPdf = async () => {
        try {
            const response = await api.get(`/api/quotes/${id}/pdf`, {
                responseType: 'blob'
            });
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const a = document.createElement('a');
            a.href = url;
            a.download = `quote-${id}.pdf`;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            window.URL.revokeObjectURL(url);
        } catch (err) {
            console.error('PDF error:', err);
            alert('Failed to download PDF');
        }
    };


    const getStatusBadge = (status) => {
        const styles = {
            DRAFT: 'bg-gray-100 text-gray-600',
            SENT: 'bg-yellow-100 text-yellow-700',
            ACCEPTED: 'bg-green-100 text-green-700',
            REJECTED: 'bg-red-100 text-red-700',
        };
        return (
            <span className={`px-2 py-1 rounded text-xs font-medium ${styles[status] || styles.DRAFT}`}>
                {status}
            </span>
        );
    };

    if (loading) return (
        <div className="min-h-screen bg-gray-100 flex items-center justify-center">
            <p className="text-gray-500">Loading quote...</p>
        </div>
    );

    return (
        <div className="min-h-screen bg-gray-100">
            <nav className="bg-white shadow px-6 py-4 flex justify-between items-center">
                <h1 className="text-xl font-bold text-gray-800">Quote Details</h1>
                <button
                    onClick={() => navigate('/dashboard')}
                    className="text-sm text-gray-600 hover:text-gray-800 font-medium"
                >
                    ← Back to Dashboard
                </button>
            </nav>

            <div className="max-w-4xl mx-auto px-6 py-8">
                {error && (
                    <div className="bg-red-100 text-red-700 px-4 py-2 rounded mb-4">{error}</div>
                )}

                {quote && (
                    <>
                        <div className="bg-white rounded-lg shadow p-6 mb-6">
                            <div className="flex justify-between items-start">
                                <div>
                                    <p className="text-sm text-gray-500">Quote ID</p>
                                    <p className="font-mono text-gray-800">{quote.id}</p>
                                    <p className="text-sm text-gray-500 mt-3">Created</p>
                                    <p className="text-gray-800">
                                        {quote.createdAt
                                            ? new Date(quote.createdAt).toLocaleDateString()
                                            : 'N/A'}
                                    </p>
                                </div>
                                <div className="text-right">
                                    <p className="text-sm text-gray-500 mb-1">Status</p>
                                    {getStatusBadge(quote.status)}
                                </div>
                            </div>
                        </div>

                        <div className="bg-white rounded-lg shadow mb-6">
                            <div className="px-6 py-4 border-b">
                                <h3 className="text-lg font-medium text-gray-700">Line Items</h3>
                            </div>
                            {lines.length === 0 ? (
                                <p className="p-6 text-center text-gray-400">No line items</p>
                            ) : (
                                <table className="w-full">
                                    <thead className="bg-gray-50 text-left">
                                    <tr>
                                        <th className="px-6 py-3 text-xs font-medium text-gray-500 uppercase">Item</th>
                                        <th className="px-6 py-3 text-xs font-medium text-gray-500 uppercase">Qty</th>
                                        <th className="px-6 py-3 text-xs font-medium text-gray-500 uppercase">Unit Price</th>
                                        <th className="px-6 py-3 text-xs font-medium text-gray-500 uppercase">Total</th>
                                    </tr>
                                    </thead>
                                    <tbody className="divide-y divide-gray-100">
                                    {lines.map((line, index) => (
                                        <tr key={index}>
                                            <td className="px-6 py-4 text-sm text-gray-800">
                                                {line.assembly?.name || 'N/A'}
                                            </td>
                                            <td className="px-6 py-4 text-sm text-gray-600">
                                                {line.quantity}
                                            </td>
                                            <td className="px-6 py-4 text-sm text-gray-600">
                                                ${line.unitPrice?.toFixed(2)}
                                            </td>
                                            <td className="px-6 py-4 text-sm font-medium text-gray-800">
                                                ${line.lineTotal?.toFixed(2)}
                                            </td>
                                        </tr>
                                    ))}
                                    </tbody>
                                </table>
                            )}
                        </div>

                        <div className="bg-white rounded-lg shadow p-6 flex justify-between items-center mb-4">
                            <span className="text-lg font-semibold text-gray-700">Total</span>
                            <span className="text-2xl font-bold text-blue-600">
                                ${Number(total).toFixed(2)}
                            </span>
                        </div>

                        <button
                            onClick={downloadPdf}
                            className="w-full bg-blue-600 text-white py-2 px-4 rounded hover:bg-blue-700 transition duration-200 font-medium"
                        >
                            Download PDF
                        </button>
                    </>
                )}
            </div>
        </div>
    );
}

export default QuoteView;