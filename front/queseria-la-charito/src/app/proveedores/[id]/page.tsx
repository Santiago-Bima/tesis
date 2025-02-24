'use client'

import React from 'react'
import ProveedorForm from './proveedorForm'

export default function ProveedorPage({ params }: { params: { id: string } }) {
  return (
    <div className="container mx-auto px-4 py-8">
      <ProveedorForm id={params.id} />
    </div>
  )
}