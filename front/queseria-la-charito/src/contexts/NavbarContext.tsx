'use client'

import React, { createContext, useCallback, useContext, useState } from 'react';

const NavbarContext = createContext({
    reload: false,
    reloadNavbar: () => {},
  });

export const NavbarProvider = ({ children }) => {
  const [reload, setReload] = useState(false);

  const reloadNavbar = useCallback(() => {
    setReload(prev => !prev); 
  }, []);

  return (
    <NavbarContext.Provider value={{ reload, reloadNavbar }}>
      {children}
    </NavbarContext.Provider>
  );
};

export const useNavbar = () => useContext(NavbarContext);
