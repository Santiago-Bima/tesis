'use client'

// contexts/ModificationContext.tsx
import React, { createContext, useState, useContext, useEffect } from 'react';
import { useSession } from './SessionContext'; // Assume this exists for managing user sessions

interface Modification {
  id: number;
  // Add other relevant fields
}

interface ModificationContextType {
  hasNewModifications: boolean;
  setHasNewModifications: React.Dispatch<React.SetStateAction<boolean>>;
  fetchModifications: () => Promise<void>;
  clearNotifications: () => void;
}

const ModificationContext = createContext<ModificationContextType | undefined>(undefined);

export const ModificationProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [hasNewModifications, setHasNewModifications] = useState(false);
  const { user } = useSession(); // Assume this hook exists to get the current user

  const fetchModifications = async () => {
    if (user?.rol === 'Subgerente') {
      try {
        const response = await fetch('http://localhost:8082/modificaciones/nuevas');
        if (response.ok) {
          const data: Modification[] = await response.json();
          setHasNewModifications(data.length > 0);
        }
      } catch (error) {
        console.error('Error fetching modifications:', error);
      }
    }
  };

  const clearNotifications = () => {
    setHasNewModifications(false);
  };

  useEffect(() => {
    if (user?.rol === 'Subgerente') {
      fetchModifications();
    }
  }, [user]);

  return (
    <ModificationContext.Provider value={{ hasNewModifications, setHasNewModifications, fetchModifications, clearNotifications }}>
      {children}
    </ModificationContext.Provider>
  );
};

export const useModifications = () => {
  const context = useContext(ModificationContext);
  if (context === undefined) {
    throw new Error('useModifications must be used within a ModificationProvider');
  }
  return context;
};