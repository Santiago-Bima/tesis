import FormulaForm from './formulaForm'

export default function FormulaPage({ params }: { params: { codigo: string } }) {
  return <FormulaForm codigo={params.codigo} />
}