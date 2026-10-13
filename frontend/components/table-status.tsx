import {
    Accordion,
    AccordionContent,
    AccordionItem,
    AccordionTrigger,
  } from "@/components/ui/accordion"
  import { DataTable } from "./data-table"
  import { Badge } from "@/components/ui/badge"
  import { AddRecordDialog } from "./add-record-dialog"
  import { EditRecordDialog } from "./edit-record-dialog"
  import { Button } from "@/components/ui/button"
  import { useToast } from "@/hooks/use-toast"
  import axios from 'axios'
  
  interface TableInfo {
    name: string
    exists: boolean
    rowCount: number
    data?: any[]
  }
  
  interface TableStatusProps {
    tables: TableInfo[]
    hasTables: boolean
    onUpdate: () => void
  }
  
  export function TableStatus({ tables, hasTables, onUpdate }: TableStatusProps) {
    const { toast } = useToast()
  
    const getIdColumnName = (tableName: string) => {
      switch (tableName.toLowerCase()) {
        case "users":
          return "userId";
        case "director":
          return "dirId";
        case "genre":
          return "genreId";
        case "movies":
          return "movieId";
        case "billing":
          return "planId";
        case "transactions":
          return "transId";
        default:
          return `${tableName.toLowerCase()}id`;
      }
    };
  
    const handleDelete = async (tableName: string, row: any) => {
      try {
        const idColumn = getIdColumnName(tableName);
        const actualIdKey = Object.keys(row).find(
          key => key.toLowerCase() === idColumn.toLowerCase()
        );
        
        if (!actualIdKey) {
          toast({
            variant: "destructive",
            title: "Error",
            description: `Missing ID column: ${idColumn}`,
          });
          return;
        }
  
        const id = row[actualIdKey];
        
        if (tableName.toLowerCase() === 'users') {
          const confirmed = window.confirm(
            "Warning: Deleting this user will also delete all related records (transactions, billing info, etc). Are you sure you want to continue?"
          );
          if (!confirmed) {
            return;
          }
        }
  
        try {
          await axios.delete(`http://localhost:8080/api/deleteRecord/${tableName}/${id}`);
          toast({
            title: "Success",
            description: "Record deleted successfully",
          });
          onUpdate();
        } catch (error: any) {
          if (error.response?.data?.message?.includes('integrity constraint')) {
            toast({
              variant: "destructive",
              title: "Error",
              description: "This record cannot be deleted because it is referenced by other tables. Please delete the related records first.",
            });
          } else {
            toast({
              variant: "destructive",
              title: "Error",
              description: error.response?.data?.message || "Failed to delete record",
            });
          }
        }
      } catch (error: any) {
        toast({
          variant: "destructive",
          title: "Error",
          description: "An unexpected error occurred",
        });
      }
    };
  
    if (!hasTables) {
      return (
        <div className="text-center p-8 border rounded-lg bg-muted">
          <p className="text-lg text-muted-foreground">No tables exist in the database yet</p>
        </div>
      )
    }
  
    return (
      <Accordion type="single" collapsible className="w-full">
        {tables.map((table) => (
          <AccordionItem key={table.name} value={table.name}>
            <AccordionTrigger className="hover:no-underline">
              <div className="flex items-center gap-4">
                <span>{table.name}</span>
                <Badge variant={table.exists ? "default" : "destructive"}>
                  {table.exists ? `${table.rowCount} rows` : "Not created"}
                </Badge>
              </div>
            </AccordionTrigger>
            <AccordionContent>
              {table.exists && (
                <div className="space-y-4">
                  <div className="flex justify-end">
                    <AddRecordDialog
                      tableName={table.name}
                      columns={table.data && table.data.length > 0 ? Object.keys(table.data[0]) : []}
                      onSuccess={onUpdate}
                    />
                  </div>
                  {table.data && table.data.length > 0 ? (
                    <div className="rounded-md border">
                      <table className="w-full">
                        <thead>
                          <tr>
                            {Object.keys(table.data[0]).map((column) => (
                              <th key={column} className="p-2 text-left">{column}</th>
                            ))}
                            <th className="p-2 text-right">Actions</th>
                          </tr>
                        </thead>
                        <tbody>
                          {table.data.map((row, index) => (
                            <tr key={index} className="border-t">
                              {Object.values(row).map((value, i) => (
                                <td key={i} className="p-2">{value?.toString()}</td>
                              ))}
                              <td className="p-2 text-right space-x-2">
                                <EditRecordDialog
                                  tableName={table.name}
                                  record={row}
                                  onSuccess={onUpdate}
                                />
                                <Button
                                  variant="destructive"
                                  size="sm"
                                  onClick={() => handleDelete(table.name, row)}
                                >
                                  Delete
                                </Button>
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  ) : (
                    <p className="text-sm text-muted-foreground p-4">
                      {table.exists ? "Table is empty" : "Table does not exist"}
                    </p>
                  )}
                </div>
              )}
            </AccordionContent>
          </AccordionItem>
        ))}
      </Accordion>
    )
  }