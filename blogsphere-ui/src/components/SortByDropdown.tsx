import React from 'react';
import { Dropdown, DropdownTrigger, DropdownMenu, DropdownItem, Button } from '@nextui-org/react';
import { ChevronDown } from 'lucide-react';

interface SortByDropdownProps {
  sortBy: string;
  onSortChange: (sort: string) => void;
}

const sortOptions = [
  { key: 'createdAt,desc', label: 'Newest' },
  { key: 'createdAt,asc', label: 'Oldest' },
  { key: 'title,asc', label: 'Title (A-Z)' },
  { key: 'title,desc', label: 'Title (Z-A)' },
];

const SortByDropdown: React.FC<SortByDropdownProps> = ({ sortBy, onSortChange }) => {
  const selectedOption = sortOptions.find((option) => option.key === sortBy);

  return (
    <Dropdown>
      <DropdownTrigger>
        <Button variant="flat" endContent={<ChevronDown size={16} />}>
          Sort by: {selectedOption?.label}
        </Button>
      </DropdownTrigger>
      <DropdownMenu
        aria-label="Sort by"
        selectionMode="single"
        selectedKeys={[sortBy]}
                onSelectionChange={(keys) => {
          const key = (keys as Set<string>).values().next().value;
          if (key) {
            onSortChange(key);
          }
        }}
      >
        {sortOptions.map((option) => (
          <DropdownItem key={option.key}>{option.label}</DropdownItem>
        ))}
      </DropdownMenu>
    </Dropdown>
  );
};

export default SortByDropdown;