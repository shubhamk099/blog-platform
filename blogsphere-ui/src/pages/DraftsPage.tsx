import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Card, CardHeader, CardBody, Button } from "@nextui-org/react";
import { Plus } from "lucide-react";
import { Link } from "react-router-dom";
import { apiService, Post } from "../services/apiService";
import PostList from "../components/PostList";
import SortByDropdown from "../components/SortByDropdown";

const DraftsPage: React.FC = () => {
  const navigate = useNavigate();
  const [drafts, setDrafts] = useState<Post[] | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [sortBy, setSortBy] = useState("updatedAt,desc");

  // Check authentication on mount
  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login");
      return;
    }
  }, [navigate]);

  useEffect(() => {
    const fetchDrafts = async () => {
      try {
        setLoading(true);
        console.log("Fetching drafts with params:", {
          page: page,
          size: 10,
          sort: sortBy,
        });

        const { posts, totalPages } = await apiService.getDrafts({
          page: page, // Changed from page - 1 to match HomePage
          size: 10,
          sort: sortBy,
        });

        console.log("Drafts received:", posts);
        console.log("Total pages:", totalPages);

        setDrafts(posts);
        setTotalPages(totalPages);
        setError(null);
      } catch (err) {
        console.error("Error loading drafts:", err);
        setError("Failed to load drafts. Please try again later.");
      } finally {
        setLoading(false);
      }
    };

    fetchDrafts();
  }, [page, sortBy]);

  const handleSortChange = (sort: string) => {
    setPage(1);
    setSortBy(sort);
  };

  return (
    <div className="max-w-4xl mx-auto px-4">
      <Card>
        <CardHeader className="flex justify-between items-center">
          <h1 className="text-2xl font-bold">My Drafts</h1>
          <div className="flex gap-2">
            <SortByDropdown sortBy={sortBy} onSortChange={handleSortChange} />
            <Button
              as={Link}
              to="/posts/new"
              color="primary"
              startContent={<Plus size={16} />}
            >
              New Post
            </Button>
          </div>
        </CardHeader>
        <CardBody>
          {error && (
            <div className="mb-4 p-4 text-red-500 bg-red-50 rounded-lg">
              {error}
            </div>
          )}
          <PostList
            posts={drafts}
            loading={loading}
            error={error}
            page={page}
            totalPages={totalPages}
            onPageChange={setPage}
          />
          {drafts?.length === 0 && !loading && (
            <div className="text-center py-8 text-default-500">
              <p>You don't have any draft posts yet.</p>
              <Button
                as={Link}
                to="/posts/new"
                color="primary"
                variant="flat"
                className="mt-4"
              >
                Create Your First Post
              </Button>
            </div>
          )}
        </CardBody>
      </Card>
    </div>
  );
};

export default DraftsPage;
