import { createContext } from 'react';
import { AuthContextType } from '../components/AuthContext';

export const AuthContext = createContext<AuthContextType | null>(null);
