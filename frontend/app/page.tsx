"use client"

import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { DataTable } from "@/components/data-table"
import axios from 'axios'
import { useState, useEffect } from "react"
import { ReloadIcon } from "@radix-ui/react-icons"
import { DeleteAlert } from "@/components/delete-alert"
import { TableStatus } from "@/components/table-status"
import { useToast } from "@/hooks/use-toast"
import { DatabaseViews } from "@/components/database-views"

type QueryResults = {
  subscribedUsers: any[]
  subscriptionTransactions: any[]
  userPlans: any[]
  highRatedMovies: any[]
  longDurationPlans: any[]
  aliceTransactions: any[]
  jamesCameronMovies: any[]
  actionMovies: any[]
  inceptionReviews: any[]
  directorMovieCounts: any[]
  planRevenue: any[]
  userAverageRatings: any[]
  moviesPerGenre: any[]
  genreAverageRatings: any[]
  topDirectors: any[]
  subscribersPerPlan: any[]
  inceptionReviewers: any[]
  nolanOrActionMovies: any[]
  actionMoviesNotByNolan: any[]
  directorsWithMultipleMovies: any[]
  genresWithHighRatedMovies: any[]
}

type ViewResults = {
  subscribedUsersPlans: any[]
  highRatedMovies: any[]
  movieReviews: any[]
  userTransactions: any[]
}

export default function Home() {
  const [loading, setLoading] = useState<string | null>(null)
  const [queryResults, setQueryResults] = useState<QueryResults | null>(null)
  const [tableStatus, setTableStatus] = useState<{tables: any[], hasTables: boolean} | null>(null)
  const [viewResults, setViewResults] = useState<ViewResults | null>(null)
  const { toast } = useToast()

  const fetchTableStatus = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/tableStatus')
      setTableStatus(response.data)
    } catch (error) {
      console.error('Error fetching table status:', error)
      toast({
        variant: "destructive",
        title: "Uh oh! Something went wrong.",
        description: "There was a problem with your request.",
      })
    }
  }

  const fetchViews = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/views')
      setViewResults(response.data)
    } catch (error) {
      console.error('Error fetching views:', error)
      toast({
        variant: "destructive",
        title: "Uh oh! Something went wrong.",
        description: "There was a problem fetching the database views.",
      })
    }
  }

  useEffect(() => {
    fetchTableStatus()
    fetchViews()
  }, [])

  const handleOperation = async (operation: string, method: string) => {
    setLoading(operation)
    try {
      const response = await axios({
        method,
        url: `http://localhost:8080/api/${operation}`
      })
      
      if (operation === 'createTables') {
        await axios.post('http://localhost:8080/api/createViews')
      } else if (operation === 'queryTables') {
        setQueryResults(response.data)
      } else if (operation === 'dropTables') {
        setQueryResults(null)
        setViewResults(null)
      }

      if (operation === 'createTables' || operation === 'populateTables' || operation === 'queryTables') {
        toast({
          title: `${operation} completed successfully`,
          variant: "default",
        })
        if (operation === 'createTables' || operation === 'populateTables') {
          fetchViews()
        }
      } else if (operation === 'dropTables') {
        toast({
          title: `${operation} completed successfully`,
          variant: "destructive",
        })
      }

      fetchTableStatus()
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Uh oh! Something went wrong.",
        description: "There was a problem with your request.",
      })
    }
    setLoading(null)
  }

  return (
    <div className="flex h-screen">
      <aside className="w-64 bg-card border-r">
        <div className="p-6">
          <h1 className="text-2xl font-bold mb-8">
            Movie Platform DB
          </h1>
          
          <div className="space-y-4">
            <Button 
              className="w-full"
              variant="default"
              disabled={!!loading}
              onClick={() => handleOperation('createTables', 'post')}
            >
              {loading === 'createTables' && <ReloadIcon className="animate-spin" />}
              Create Tables
            </Button>

            <Button
              className="w-full"
              variant="default"
              disabled={!!loading}
              onClick={() => handleOperation('populateTables', 'post')}
            >
              {loading === 'populateTables' && <ReloadIcon className="animate-spin" />}
              Populate Tables
            </Button>

            <Button
              className="w-full"
              variant="default"
              disabled={!!loading}
              onClick={() => handleOperation('queryTables', 'get')}
            >
              {loading === 'queryTables' && <ReloadIcon className="animate-spin" />}
              Query Tables
            </Button>

            <DeleteAlert 
              loading={loading === 'dropTables'}
              onConfirm={() => handleOperation('dropTables', 'delete')}
            />
          </div>
        </div>
      </aside>

      <main className="flex-1 overflow-auto p-8">
        {tableStatus && (
          <Card className="mb-8">
            <div className="p-6">
              <h2 className="text-xl font-semibold mb-4">Database Tables</h2>
              <TableStatus {...tableStatus} onUpdate={fetchTableStatus} />
            </div>
          </Card>
        )}

        {viewResults && <DatabaseViews views={viewResults} />}

        {queryResults && (
          <Card className="mb-8">
            <div className="p-6">
              <h2 className="text-xl font-semibold mb-4">Query Results</h2>
              <div className="space-y-6">
                <DataTable 
                  title="Subscribed Users" 
                  data={queryResults.subscribedUsers} 
                />
                <DataTable 
                  title="Subscription Transactions" 
                  data={queryResults.subscriptionTransactions} 
                />
                <DataTable 
                  title="User Plans" 
                  data={queryResults.userPlans} 
                />
                <DataTable 
                  title="High Rated Movies" 
                  data={queryResults.highRatedMovies} 
                />
                <DataTable 
                  title="Long Duration Plans" 
                  data={queryResults.longDurationPlans} 
                />
                <DataTable 
                  title="Alice Johnson's Transactions" 
                  data={queryResults.aliceTransactions} 
                />
                <DataTable 
                  title="James Cameron Movies" 
                  data={queryResults.jamesCameronMovies} 
                />
                <DataTable 
                  title="Action Movies" 
                  data={queryResults.actionMovies} 
                />
                <DataTable 
                  title="Inception Reviews" 
                  data={queryResults.inceptionReviews} 
                />  
                <DataTable 
                  title="Director Movie Counts" 
                  data={queryResults.directorMovieCounts} 
                />
                <DataTable 
                  title="Plan Revenue" 
                  data={queryResults.planRevenue} 
                />
                <DataTable 
                  title="User Average Ratings" 
                  data={queryResults.userAverageRatings} 
                  />
                <DataTable 
                  title="Movies per Genre" 
                  data={queryResults.moviesPerGenre} 
                />
                <DataTable 
                  title="Genre Average Ratings" 
                  data={queryResults.genreAverageRatings} 
                />
                <DataTable 
                  title="Top Directors" 
                  data={queryResults.topDirectors} 
                />
                <DataTable 
                  title="Subscribers per Plan" 
                  data={queryResults.subscribersPerPlan} 
                />
                <DataTable 
                  title="Inception Reviewers" 
                  data={queryResults.inceptionReviewers} 
                />
                <DataTable 
                  title="Nolan or Action Movies" 
                  data={queryResults.nolanOrActionMovies} 
                />
                <DataTable 
                  title=  "Action Movies Not by Nolan" 
                  data={queryResults.actionMoviesNotByNolan} 
                />
                <DataTable 
                  title="Directors with Multiple Movies" 
                  data={queryResults.directorsWithMultipleMovies} 
                />
                <DataTable 
                  title="Genres with High-Rated Movies" 
                  data={queryResults.genresWithHighRatedMovies} 
                />
              </div>
            </div>
          </Card>
        )}
      </main>
    </div>
  )
}