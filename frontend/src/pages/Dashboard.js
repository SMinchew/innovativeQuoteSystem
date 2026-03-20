import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axiosConfig';


function Dashboard() {
    const navigate = useNavigate();
    const [quotes, setQuotes] = useState([]);
    const [loading, setLoading] = useState(false);
    const [actionLoading, setActionLoading] = useState(null);

    useEffect(() => {
        fetchQuotes();
    }, []);

    const fetchQuotes = async () => {
        setLoading(true);
        try {
            const response = await api.get('/api/quotes');
            setQuotes(Array.isArray(response.data) ? response.data : []);
        } catch (err) {
            console.error('Failed to load quotes', err);
            setQuotes([]);
        }
        setLoading(false);
    };

    const deleteQuote = async (id) => {
        if (!window.confirm("Are you sure you want to permanently delete this quote?")) return;
        setActionLoading(id);
        try {
            await api.delete(`/api/quotes/${id}`);
            setQuotes(quotes.filter(q => q.id !== id));
        } catch (err) {
            alert("Failed to delete quote.");
        }
        setActionLoading(null);
    };

    const sendToQuickBooks = async (id) => {
        setActionLoading(id);
        try {
            await api.put(`/api/quotes/${id}/status?status=PENDING_QB`);
            fetchQuotes();
        } catch (err) {
            alert("Failed to send to QuickBooks.");
        }
        setActionLoading(null);
    };

    const handleLogout = () => {
        localStorage.removeItem('token');
        navigate('/login');
    };

    const getStatusBadge = (status) => {
        const styles = {
            DRAFT: 'bg-gray-100 text-gray-600 border-gray-200',
            PENDING_QB: 'bg-blue-100 text-blue-700 border-blue-200',
            IN_QUICKBOOKS: 'bg-green-100 text-green-700 border-green-200',
            SENT: 'bg-yellow-100 text-yellow-700 border-yellow-200',
        };
        const label = status === 'PENDING_QB' ? 'WAITING FOR SYNC' : status;
        return (
            <span className={`px-2 py-1 rounded text-[10px] font-bold border ${styles[status] || styles.DRAFT}`}>
                {label}
            </span>
        );
    };

    return (
        <div className="min-h-screen bg-gray-100">
            <nav className="bg-white shadow px-6 py-4 flex justify-between items-center">
                <h1 className="text-xl font-bold text-gray-800">Innovative Quote Builder</h1>
                <button onClick={handleLogout} className="text-sm text-red-600 hover:text-red-800 font-medium">Logout</button>
            </nav>

            <div className="max-w-6xl mx-auto px-6 py-8">
                <div className="flex justify-between items-center mb-6">
                    <h2 className="text-2xl font-semibold text-gray-700">Quote Management</h2>
                    <button
                        onClick={() => navigate('/quotes/new')}
                        className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition font-bold shadow-sm"
                    >
                        + New Quote
                    </button>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
                    <div className="bg-white rounded-xl shadow-sm p-6 border-l-4 border-blue-500">
                        <p className="text-xs font-bold text-gray-400 uppercase tracking-wider">Total Quotes</p>
                        <p className="text-3xl font-black text-gray-800 mt-1">{loading ? '...' : quotes.length}</p>
                    </div>
                    <div className="bg-white rounded-xl shadow-sm p-6 border-l-4 border-yellow-500">
                        <p className="text-xs font-bold text-gray-400 uppercase tracking-wider">Waiting for QB</p>
                        <p className="text-3xl font-black text-yellow-600 mt-1">
                            {quotes.filter(q => q.status === 'PENDING_QB').length}
                        </p>
                    </div>
                    <div className="bg-white rounded-xl shadow-sm p-6 border-l-4 border-green-500">
                        <p className="text-xs font-bold text-gray-400 uppercase tracking-wider">Synced to QB</p>
                        <p className="text-3xl font-black text-green-600 mt-1">
                            {quotes.filter(q => q.status === 'IN_QUICKBOOKS').length}
                        </p>
                    </div>
                </div>

                <div className="bg-white rounded-xl shadow-sm overflow-hidden">
                    <div className="px-6 py-4 border-b bg-gray-50 flex justify-between items-center">
                        <h3 className="text-sm font-bold text-gray-600 uppercase">Recent Activity</h3>
                        <button onClick={fetchQuotes} className="text-xs text-blue-600 hover:underline">Refresh List</button>
                    </div>

                    {loading ? (
                        <div className="p-12 text-center text-gray-400 italic">Updating Dashboard...</div>
                    ) : (
                        <table className="w-full text-left border-collapse">
                            <thead className="bg-white border-b">
                            <tr>
                                <th className="px-6 py-4 text-xs font-bold text-gray-400 uppercase">Customer</th>
                                <th className="px-6 py-4 text-xs font-bold text-gray-400 uppercase">Created On</th>
                                <th className="px-6 py-4 text-xs font-bold text-gray-400 uppercase">Created By</th>
                                <th className="px-6 py-4 text-xs font-bold text-gray-400 uppercase">Status</th>
                                <th className="px-6 py-4 text-xs font-bold text-gray-400 uppercase text-right">Actions</th>
                            </tr>
                            </thead>
                            <tbody className="divide-y divide-gray-100">
                            {quotes.map(quote => (
                                <tr key={quote.id} className="hover:bg-blue-50/30 transition-colors">
                                    <td className="px-6 py-4">
                                        <p className="text-sm font-bold text-gray-800">{quote.customerName || 'Walk-In'}</p>
                                    </td>
                                    <td className="px-6 py-4">
                                        <p className="text-xs text-gray-600 font-medium">
                                            {quote.createdAt ? new Date(quote.createdAt).toLocaleDateString() : 'N/A'}
                                        </p>
                                    </td>
                                    <td className="px-6 py-4">
                                        <span className="text-xs font-semibold text-gray-600 bg-gray-100 px-2 py-1 rounded">
                                            {quote.createdBy || 'System'}
                                        </span>
                                    </td>
                                    <td className="px-6 py-4">
                                        {getStatusBadge(quote.status)}
                                    </td>
                                    <td className="px-6 py-4 text-right">
                                        <div className="flex justify-end gap-3">
                                            {quote.status === 'DRAFT' && (
                                                <button
                                                    disabled={actionLoading === quote.id}
                                                    onClick={() => sendToQuickBooks(quote.id)}
                                                    className="text-xs font-bold text-blue-600 hover:text-blue-800 disabled:opacity-50"
                                                >
                                                    Send to QB
                                                </button>
                                            )}
                                            <button
                                                onClick={() => navigate(`/quotes/${quote.id}`)}
                                                className="text-xs font-bold text-gray-500 hover:text-gray-700"
                                            >
                                                View
                                            </button>
                                            <button
                                                disabled={actionLoading === quote.id}
                                                onClick={() => deleteQuote(quote.id)}
                                                className="text-xs font-bold text-red-400 hover:text-red-600 disabled:opacity-50"
                                            >
                                                Delete
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    )}
                </div>
            </div>
        </div>
    );
}

export default Dashboard;