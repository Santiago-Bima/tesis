'use client'

import React, { createContext, useCallback, useContext, useState } from 'react';

const FAQContext = createContext({
    reload: false,
    reloadFAQ: () => {},
  });

export const FAQProvider = ({ children }) => {
  const [reload, setReload] = useState(false);

  const reloadFAQ = useCallback(() => {
    setReload(prev => !prev); 
  }, []);

  return (
    <FAQContext.Provider value={{ reload, reloadFAQ }}>
      {children}
    </FAQContext.Provider>
  );
};

export const useFAQ = () => useContext(FAQContext);
