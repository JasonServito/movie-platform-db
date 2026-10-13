import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
  } from "@/components/ui/table"
  
  interface DataTableProps {
    data: any[]
    title: string
  }
  
  export function DataTable({ data, title }: DataTableProps) {
    if (!data || data.length === 0) return null
  
    const columns = Object.keys(data[0])
  
    return (
      <div className="rounded-md border">
        <h3 className="text-lg font-semibold p-4">{title}</h3>
        <Table>
          <TableHeader>
            <TableRow>
              {columns.map((column) => (
                <TableHead key={column}>
                  {column.replace(/([A-Z])/g, ' $1').trim()}
                </TableHead>
              ))}
            </TableRow>
          </TableHeader>
          <TableBody>
            {data.map((row, i) => (
              <TableRow key={i}>
                {columns.map((column) => (
                  <TableCell key={column}>{row[column]}</TableCell>
                ))}
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>
    )
  }