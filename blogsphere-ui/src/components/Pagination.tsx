import React from 'react';
import { Pagination as NextUIPagination } from '@nextui-org/react';

interface PaginationProps {
  page: number;
  totalPages: number;
  onPageChange: (page: number) => void;
}

const Pagination: React.FC<PaginationProps> = ({ page, totalPages, onPageChange }) => {
  return (
    <div className="flex justify-center mt-6">
      <NextUIPagination
        isCompact
        showControls
        showShadow
        color="primary"
        page={page}
        total={totalPages}
        onChange={onPageChange}
      />
    </div>
  );
};

export default Pagination;