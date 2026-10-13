import { useState } from "react"
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogTrigger,
} from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { useToast } from "@/hooks/use-toast"
import axios from 'axios'

interface EditRecordDialogProps {
  tableName: string
  record: any
  onSuccess: () => void
}

export function EditRecordDialog({ tableName, record, onSuccess }: EditRecordDialogProps) {
  const [formData, setFormData] = useState(record)
  const [loading, setLoading] = useState(false)
  const { toast } = useToast()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    
    try {
      // Convert any number strings to actual numbers
      const processedData = Object.entries(formData).reduce((acc, [key, value]) => {
        if (typeof value === 'string' && !isNaN(Number(value))) {
          acc[key] = Number(value);
        } else {
          acc[key] = value;
        }
        return acc;
      }, {} as Record<string, any>);

      await axios.put(`http://localhost:8080/api/updateRecord/${tableName}`, processedData)
      toast({
        title: "Success",
        description: "Record updated successfully",
      })
      onSuccess()
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || error.message || "Failed to update record"
      toast({
        variant: "destructive",
        title: "Error",
        description: errorMessage,
      })
    } finally {
      setLoading(false)
    }
  }

  return (
    <Dialog>
      <DialogTrigger asChild>
        <Button variant="outline" size="sm">Edit</Button>
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Edit {tableName} Record</DialogTitle>
          <DialogDescription>
            Make changes to the record below.
          </DialogDescription>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          {Object.entries(record).map(([key, value]) => (
            <div key={key}>
              <Label htmlFor={key}>{key}</Label>
              <Input
                id={key}
                value={formData[key] || ""}
                onChange={(e) => setFormData({ ...formData, [key]: e.target.value })}
                disabled={key === `${tableName.toLowerCase()}id`}
              />
            </div>
          ))}
          <Button type="submit" disabled={loading}>
            {loading ? "Saving..." : "Save Changes"}
          </Button>
        </form>
      </DialogContent>
    </Dialog>
  )
}