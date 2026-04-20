import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axiosConfig';

function Register() {
    const [form, setForm] = useState({ username: '', email: '', password: '', companyCode: '' });
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleRegister = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        try {
            await api.post('/api/auth/register', form);
            setSuccess('Registration successful! Please check your email to verify your account.');
        } catch (err) {
            setError(err.response?.data || 'Registration failed');
        }
        setLoading(false);
    };

    return (
        <div className="min-h-screen bg-gray-100 flex items-center justify-center">
            <div className="bg-white p-8 rounded-lg shadow-md w-full max-w-md">
                <h1 className="text-2xl font-bold text-gray-800 mb-2">Create Account</h1>
                <p className="text-gray-500 mb-6">Innovative Quote Builder</p>

                {error && <div className="bg-red-100 text-red-700 px-4 py-2 rounded mb-4">{error}</div>}
                {success && <div className="bg-green-100 text-green-700 px-4 py-2 rounded mb-4">{success}</div>}

                {!success && (
                    <form onSubmit={handleRegister}>
                        <div className="mb-4">
                            <label className="block text-sm font-medium text-gray-700 mb-1">Username</label>
                            <input type="text" name="username" value={form.username} onChange={handleChange}
                                   className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                                   placeholder="Choose a username" required />
                        </div>
                        <div className="mb-4">
                            <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
                            <input type="email" name="email" value={form.email} onChange={handleChange}
                                   className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                                   placeholder="Enter your email" required />
                        </div>
                        <div className="mb-4">
                            <label className="block text-sm font-medium text-gray-700 mb-1">Password</label>
                            <input type="password" name="password" value={form.password} onChange={handleChange}
                                   className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                                   placeholder="Choose a password" required />
                        </div>
                        <div className="mb-6">
                            <label className="block text-sm font-medium text-gray-700 mb-1">Company Code</label>
                            <input type="password" name="companyCode" value={form.companyCode} onChange={handleChange}
                                   className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                                   placeholder="Enter your company code" required />
                        </div>
                        <button type="submit" disabled={loading}
                                className="w-full bg-blue-600 text-white py-2 px-4 rounded hover:bg-blue-700 transition duration-200 font-medium disabled:opacity-50">
                            {loading ? 'Creating Account...' : 'Create Account'}
                        </button>
                    </form>
                )}

                <p className="text-center text-sm text-gray-500 mt-4">
                    Already have an account?{' '}
                    <button onClick={() => navigate('/login')} className="text-blue-600 hover:underline font-medium">
                        Sign In
                    </button>
                </p>
            </div>
        </div>
    );
}

export default Register;