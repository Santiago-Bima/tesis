import React from 'react';
import InsumosForm from './insumosForm'

export default function InsumoPage({ params }: { params: { id: string } }) {
  return <InsumosForm id={params.id} />
}