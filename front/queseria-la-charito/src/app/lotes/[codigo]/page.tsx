import LoteForm from './loteForm'

export default function LoteEditPage({ params }: { params: { codigo: string } }) {
  return <LoteForm codigo={params.codigo} />
}