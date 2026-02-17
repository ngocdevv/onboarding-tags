// Icon names from MaterialCommunityIcons (@expo/vector-icons)
export const TAGS = [
    { id: '1', label: 'Code', color: '#ffe4e1', icon: 'code-tags' },
    { id: '2', label: 'Design', color: '#fffacd', icon: 'brush' },
    { id: '3', label: 'Music', color: '#e0ffff', icon: 'music-note' },
    { id: '4', label: 'Travel', color: '#f0e68c', icon: 'airplane' },
    { id: '5', label: 'Finance', color: '#dda0dd', icon: 'wallet-outline' },
    { id: '6', label: 'Cook', color: '#90ee90', icon: 'chef-hat' },
    { id: '7', label: 'Meditate', color: '#add8e6', icon: 'meditation' },
    { id: '8', label: 'Work', color: '#ffb6c1', icon: 'laptop' },
    { id: '9', label: 'Learn', color: '#fafad2', icon: 'book-open-variant' },
    { id: '10', label: 'Exercise', color: '#87cefa', icon: 'run-fast' },
    { id: '11', label: 'Photo', color: '#d3d3d3', icon: 'camera-outline' },
    { id: '12', label: 'Garden', color: '#98fb98', icon: 'flower-outline' },
    { id: '13', label: 'Explore', color: '#afeeee', icon: 'compass-outline' },
    { id: '14', label: 'Write', color: '#ffdead', icon: 'pencil-outline' },
    { id: '15', label: 'Research', color: '#e6e6fa', icon: 'flask-outline' },
];

// Glyph map for Skia rendering (MaterialCommunityIcons codepoints)
export const ICON_GLYPHS: Record<string, string> = {
    'code-tags': String.fromCodePoint(0xf0174),
    'brush': String.fromCodePoint(0xf00e3),
    'music-note': String.fromCodePoint(0xf0387),
    'airplane': String.fromCodePoint(0xf001d),
    'wallet-outline': String.fromCodePoint(0xf0bdd),
    'chef-hat': String.fromCodePoint(0xf0b7c),
    'meditation': String.fromCodePoint(0xf117b),
    'laptop': String.fromCodePoint(0xf0322),
    'book-open-variant': String.fromCodePoint(0xf14f7),
    'run-fast': String.fromCodePoint(0xf046e),
    'camera-outline': String.fromCodePoint(0xf0d5d),
    'flower-outline': String.fromCodePoint(0xf09f0),
    'compass-outline': String.fromCodePoint(0xf018c),
    'pencil-outline': String.fromCodePoint(0xf0cb6),
    'flask-outline': String.fromCodePoint(0xf0096),
};
