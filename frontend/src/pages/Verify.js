import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import api from '../api/axiosConfig';

function Verify() {
    const [searchParams] = useSearchParams();
    const [message, setMessage] = useState('Verifying your email...');
    const [success, setSuccess] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        const token = searchParams.get('token');
        if (!token) {
            setMessage('Invalid verification link.');
            return;
        }
        api.get(`/api/auth/verify?token=${token}`)
            .then(() => {
                setSuccess(true);
                setMessage('Email verified successfully! You can now log in.');
            })
            .catch(() => {
                setMessage('Invalid or expired verification link.');
            });
    }, []);

    return (
        <div className="min-h-screen bg-gray-100 flex items-center justify-center">
            <div className="bg-white p-8 rounded-lg shadow-md w-full max-w-md text-center">
                <h1 className="text-2xl font-bold text-gray-800 mb-4">Email Verification</h1>
                <p className={`mb-6 ${success ? 'text-green-600' : 'text-gray-600'}`}>{message}</p>
                {success && (
                    <button onClick={() => navigate('/login')}
                            className="bg-blue-600 text-white py-2 px-6 rounded hover:bg-blue-700 font-medium">
                        Go to Login
                    </button>
                )}
            </div>
        </div>
    );
}

export default Verify;