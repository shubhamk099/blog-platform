import { useEffect, useState } from 'react';
import { 
  Card, 
  CardHeader, 
  CardBody,
  Tabs, 
  Tab,
} from '@nextui-org/react';
import { apiService, Post, Category, Tag } from '../services/apiService';
import PostList from '../components/PostList';
import SortByDropdown from '../components/SortByDropdown';

const HomePage: React.FC = () => {
  const [posts, setPosts] = useState<Post[] | null>(null);
  const [categories, setCategories] = useState<Category[]>([]);
  const [tags, setTags] = useState<Tag[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [sortBy, setSortBy] = useState("createdAt,desc");
  const [selectedCategory, setSelectedCategory] = useState<string|undefined>(undefined);
  const [selectedTag, setSelectedTag] = useState<string | undefined>(undefined);

  useEffect(() => {
    const fetchPosts = async () => {
      try {
        setLoading(true);
        const { posts, totalPages } = await apiService.getPosts({
          page,
          sort: sortBy,
          categoryId: selectedCategory,
          tagId: selectedTag,
        });
        setPosts(posts);
        setTotalPages(totalPages);
        setError(null);
      } catch {
        setError('Failed to load posts. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    fetchPosts();
  }, [page, sortBy, selectedCategory, selectedTag]);

  useEffect(() => {
    const fetchCategoriesAndTags = async () => {
      try {
        const [categoriesResponse, tagsResponse] = await Promise.all([
          apiService.getCategories(),
          apiService.getTags(),
        ]);
        setCategories(categoriesResponse);
        setTags(tagsResponse);
      } catch {
        setError('Failed to load categories or tags.');
      }
    };

    fetchCategoriesAndTags();
  }, []);

  const handleCategoryChange = (categoryId: string|undefined) => {
    setPage(1);
    if("all" === categoryId){
      setSelectedCategory(undefined)
    } else {
      setSelectedCategory(categoryId);
    }
  };

  const handleTagChange = (tagId: string | undefined) => {
    setPage(1);
    setSelectedTag(selectedTag === tagId ? undefined : tagId);
  }

  const handleSortChange = (sort: string) => {
    setPage(1);
    setSortBy(sort);
  }

  return (
    <div className="max-w-6xl mx-auto px-4 space-y-6">
      <Card className="mb-6 px-2">
        <CardHeader className='flex justify-between'>
          <h1 className="text-2xl font-bold">Blog Posts</h1>
          <SortByDropdown sortBy={sortBy} onSortChange={handleSortChange} />
        </CardHeader>
        <CardBody>
          <div className="flex flex-col gap-4">                     
            <Tabs 
              selectedKey={selectedCategory || 'all'} 
              onSelectionChange={(key) => {
                handleCategoryChange(key as string)
              }}
              variant="underlined"
              classNames={{
                tabList: "gap-6",
                cursor: "w-full bg-primary",
              }}
            >
              <Tab key="all" title="All Posts" />
              {categories.map((category) => (
                <Tab 
                  key={category.id} 
                  title={`${category.name} (${category.postCount})`}
                />
              ))}
            </Tabs>

            {tags.length > 0 && (
              <div className="flex gap-2 flex-wrap">
                {tags.map((tag) => (
                  <button
                    key={tag.id}
                    onClick={() => handleTagChange(tag.id)}
                    className={`px-3 py-1 rounded-full text-sm ${
                      selectedTag === tag.id
                        ? 'bg-primary text-white'
                        : 'bg-default-100 hover:bg-default-200'
                    }`}
                  >
                    {tag.name} ({tag.postCount})
                  </button>
                ))}
              </div>
            )}
          </div>
        </CardBody>
      </Card>

      <PostList
        posts={posts}
        loading={loading}
        error={error}
        page={page}
        totalPages={totalPages}
        onPageChange={setPage}
      />
    </div>
  );
};

export default HomePage;