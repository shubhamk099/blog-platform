import React from "react";
import { useNavigate } from "react-router-dom";
import {
  Card,
  CardBody,
  CardFooter,
  CardHeader,
  Chip,
} from "@nextui-org/react";
import { Post } from "../services/apiService";
import { Calendar, Clock, Tag } from "lucide-react";
import DOMPurify from "dompurify";
import Pagination from "./Pagination";

interface PostListProps {
  posts: Post[] | null;
  loading: boolean;
  error: string | null;
  page: number;
  totalPages: number;
  onPageChange: (page: number) => void;
}

const PostList: React.FC<PostListProps> = ({
  posts,
  loading,
  error,
  page,
  totalPages,
  onPageChange,
}) => {
  const navigate = useNavigate();

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString("en-US", {
      year: "numeric",
      month: "long",
      day: "numeric",
    });
  };

  const createExcerpt = (content: string) => {
    // First sanitize the HTML
    const sanitizedContent = DOMPurify.sanitize(content, {
      ALLOWED_TAGS: ["p", "strong", "em", "br"],
      ALLOWED_ATTR: [],
    });

    // Create a temporary div to parse the HTML
    const tempDiv = document.createElement("div");
    tempDiv.innerHTML = sanitizedContent;

    // Get the text content and limit it
    let textContent = tempDiv.textContent || tempDiv.innerText || "";
    textContent = textContent.trim();

    // Limit to roughly 200 characters, ending at the last complete word
    if (textContent.length > 200) {
      textContent =
        textContent.substring(0, 200).split(" ").slice(0, -1).join(" ") + "...";
    }

    return textContent;
  };

  if (error) {
    return <div className="p-4 text-red-500 bg-red-50 rounded-lg">{error}</div>;
  }

  const navToPostPage = (post: Post) => {
    navigate(`/posts/${post.id}`);
  };

  return (
    <div className="w-full space-y-6">
      {loading ? (
        <div className="space-y-4">
          {[...Array(3)].map((_, index) => (
            <Card key={index} className="w-full animate-pulse">
              <CardBody className="space-y-3">
                <div className="h-6 bg-default-200 rounded w-3/4"></div>
                <div className="h-4 bg-default-200 rounded w-1/2"></div>
                <div className="h-4 bg-default-200 rounded w-full"></div>
                <div className="h-4 bg-default-200 rounded w-5/6"></div>
              </CardBody>
            </Card>
          ))}
        </div>
      ) : posts && posts.length > 0 ? (
        <>
          <div className="space-y-4">
            {posts.map((post) => (
              <Card
                key={post.id}
                className="w-full hover:shadow-lg transition-shadow"
                isPressable
                onPress={() => navToPostPage(post)}
              >
                <CardHeader className="flex-col items-start gap-2 pb-2">
                  <h2 className="text-xl font-bold text-left w-full">
                    {post.title}
                  </h2>
                  <p className="text-sm text-default-500">
                    by {post.author?.name || "Anonymous"}
                  </p>
                </CardHeader>

                <CardBody className="py-3">
                  <p className="text-default-700 line-clamp-3">
                    {createExcerpt(post.content)}
                  </p>
                </CardBody>

                <CardFooter className="flex-col items-start gap-3 pt-3">
                  <div className="flex flex-wrap items-center gap-4 w-full">
                    <div className="flex items-center gap-1.5 text-sm text-default-500">
                      <Calendar size={16} />
                      <span>{formatDate(post.createdAt)}</span>
                    </div>
                    <div className="flex items-center gap-1.5 text-sm text-default-500">
                      <Clock size={16} />
                      <span>{post.readingTime} min read</span>
                    </div>
                  </div>

                  <div className="flex flex-wrap gap-2 w-full">
                    <Chip
                      size="sm"
                      className="bg-primary text-primary-foreground"
                      variant="flat"
                    >
                      {post.category.name}
                    </Chip>
                    {post.tags.map((tag) => (
                      <Chip
                        key={tag.id}
                        size="sm"
                        className="bg-default-100 text-default-700"
                        variant="flat"
                        startContent={<Tag size={12} />}
                      >
                        {tag.name}
                      </Chip>
                    ))}
                  </div>
                </CardFooter>
              </Card>
            ))}
          </div>

          {totalPages > 1 && (
            <div className="flex justify-center mt-8">
              <Pagination
                page={page}
                totalPages={totalPages}
                onPageChange={onPageChange}
              />
            </div>
          )}
        </>
      ) : (
        <Card className="w-full">
          <CardBody className="text-center py-12">
            <p className="text-default-500 text-lg">No posts found.</p>
          </CardBody>
        </Card>
      )}
    </div>
  );
};

export default PostList;
