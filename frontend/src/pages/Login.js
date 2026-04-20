import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axiosConfig';

function Login() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const response = await api.post('/api/auth/login', {
                username,
                password
            });
            const token = response.data.token;
            localStorage.setItem('token', token);
            window.location.href = '/dashboard';
        } catch (err) {
            setError('Invalid username or password');
        }
    };

    return (
        <div className="min-h-screen bg-gray-100 flex items-center justify-center">
            <div className="bg-white p-8 rounded-lg shadow-md w-full max-w-md">
                <h1 className="text-2xl font-bold text-gray-800 mb-2">
                    Innovative Quote Builder
                </h1>
                <p className="text-gray-500 mb-6">Sign in to your account</p>

                {error && (
                    <div className="bg-red-100 text-red-700 px-4 py-2 rounded mb-4">
                        {error}
                    </div>
                )}

                <form onSubmit={handleLogin}>
                    <div className="mb-4">
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Username
                        </label>
                        <input
                            type="text"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                            placeholder="Enter your username"
                            required
                        />
                    </div>

                    <div className="mb-6">
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Password
                        </label>
                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                            placeholder="Enter your password"
                            required
                        />
                    </div>

                    <button
                        type="submit"
                        className="w-full bg-blue-600 text-white py-2 px-4 rounded hover:bg-blue-700 transition duration-200 font-medium"
                    >
                        Sign In
                    </button>
                </form>

                <p className="text-center text-sm text-gray-500 mt-4">
                    Don't have an account?{' '}
                    <button onClick={() => navigate('/register')} className="text-blue-600 hover:underline font-medium">
                        Create Account
                    </button>
                </p>
            </div>
        </div>
    );
}

export default Login;