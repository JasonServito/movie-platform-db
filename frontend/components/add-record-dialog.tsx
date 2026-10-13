import { useState } from "react"
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { useToast } from "@/hooks/use-toast"
import axios from 'axios'

interface AddRecordDialogProps {
  tableName: string
  columns: string[]
  onSuccess: () => void
}

export function AddRecordDialog({ tableName, columns, onSuccess }: AddRecordDialogProps) {
  const [formData, setFormData] = useState<Record<string, string>>({})
  const [loading, setLoading] = useState(false)
  const { toast } = useToast()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    
    try {
      await axios.post(`http://localhost:8080/api/addRecord/${tableName}`, formData)
      toast({
        title: "Success",
        description: "Record added successfully",
      })
      onSuccess()
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to add record",
      })
    } finally {
      setLoading(false)
    }
  }

  return (
    <Dialog>
      <DialogTrigger asChild>
        <Button variant="outline" size="sm">Add Record</Button>
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Add New {tableName} Record</DialogTitle>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          {columns.map((column) => (
            <div key={column}>
              <Label htmlFor={column}>{column}</Label>
              <Input
                id={column}
                value={formData[column] || ""}
                onChange={(e) => setFormData({ ...formData, [column]: e.target.value })}
              />
            </div>
          ))}
          <Button type="submit" disabled={loading}>
            Add Record
          </Button>
        </form>
      </DialogContent>
    </Dialog>
  )
}