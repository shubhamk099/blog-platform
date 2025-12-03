import React, { useState, useCallback, useEffect } from 'react';
import { AuthContext } from '../contexts/AuthContext';
import { apiService, UserDto } from '../services/apiService';

export interface AuthContextType {
  isAuthenticated: boolean;
  user: UserDto | null;
  login: (email: string, password: string) => Promise<void>;
  signup: (name: string, email: string, password: string) => Promise<void>;
  logout: () => void;
  token: string | null;
}

interface AuthProviderProps {
  children: React.ReactNode;
}



export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
  const [user, setUser] = useState<UserDto | null>(null);
  const [token, setToken] = useState<string | null>(localStorage.getItem('token'));

  const fetchUserProfile = useCallback(async () => {
    try {
      const userProfile = await apiService.getUserProfile();
      setUser(userProfile);
      setIsAuthenticated(true);
    } catch (error) {
      console.error("Failed to fetch user profile:", error);
      // If fetching user profile fails, clear authentication
      localStorage.removeItem('token');
      setIsAuthenticated(false);
      setUser(null);
      setToken(null);
    }
  }, []);

  // Initialize auth state from token
  useEffect(() => {
    const initializeAuth = async () => {
      const storedToken = localStorage.getItem('token');
      if (storedToken) {
        setToken(storedToken);
        await fetchUserProfile();
      }
    };

    initializeAuth();
  }, [fetchUserProfile]);

  const login = useCallback(async (email: string, password: string) => {
    // eslint-disable-next-line no-useless-catch
    try {
      const response = await apiService.login({ email, password });
      
      localStorage.setItem('token', response.token);
      setToken(response.token);
      
      await fetchUserProfile();
    } catch (error) {
      throw error;
    }
  }, [fetchUserProfile]);

  const signup = useCallback(async (name: string, email: string, password: string) => {
    // eslint-disable-next-line no-useless-catch
    try {
      const response = await apiService.signup({ name, email, password });

      localStorage.setItem('token', response.token);
      setToken(response.token);
      
      await fetchUserProfile();
    } catch (error) {
      throw error;
    }
  }, [fetchUserProfile]);


  const logout = useCallback(() => {
    localStorage.removeItem('token');
    setIsAuthenticated(false);
    setUser(null);
    setToken(null);
    apiService.logout(); // This clears the token from apiService
  }, []);

  // Update apiService token when it changes
  useEffect(() => {
    if (token) {
      // Update axios instance configuration
      const axiosInstance = apiService['api'];
      axiosInstance.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    }
  }, [token]);

  const value = {
    isAuthenticated,
    user,
    login,
    signup,
    logout,
    token
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};
