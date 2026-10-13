import { Card } from "@/components/ui/card"
import { DataTable } from "./data-table"

interface DatabaseViewsProps {
  views: {
    subscribedUsersPlans: any[]
    highRatedMovies: any[]
    movieReviews: any[]
    userTransactions: any[]
  } | null
}

export function DatabaseViews({ views }: DatabaseViewsProps) {
  if (!views) return null

  return (
    <Card className="mb-8">
      <div className="p-6">
        <h2 className="text-xl font-semibold mb-4">Database Views</h2>
        <div className="space-y-6">
          <DataTable 
            title="Subscribed Users Plans" 
            data={views.subscribedUsersPlans} 
          />
          <DataTable 
            title="High Rated Movies" 
            data={views.highRatedMovies} 
          />
          <DataTable 
            title="Movie Reviews" 
            data={views.movieReviews} 
          />
          <DataTable 
            title="User Transactions" 
            data={views.userTransactions} 
          />
        </div>
      </div>
    </Card>
  )
}