const fs = require('fs');
const path = require('path');

// Configuration
const MARKDOWN_FOLDER = './markdown';
const IMAGE_STYLE = 'max-width: 100%; height: auto; display: block; margin: 0 auto;';

/**
 * Recursively get all .md files in a directory
 */
function getAllMarkdownFiles(dirPath, arrayOfFiles = []) {
    const files = fs.readdirSync(dirPath);

    files.forEach(file => {
        const filePath = path.join(dirPath, file);
        
        if (fs.statSync(filePath).isDirectory()) {
            arrayOfFiles = getAllMarkdownFiles(filePath, arrayOfFiles);
        } else if (path.extname(file) === '.md') {
            arrayOfFiles.push(filePath);
        }
    });

    return arrayOfFiles;
}

/**
 * Convert Markdown image syntax to HTML img tag with responsive styling
 */
function convertMarkdownImagesToHtml(content) {
    // Regex pattern to match ![alt text](image source)
    const markdownImageRegex = /!\[([^\]]*)\]\(([^)]+)\)/g;
    
    return content.replace(markdownImageRegex, (match, altText, imageSrc) => {
        // Use provided alt text or default to "Image"
        const alt = altText.trim() || 'Image';
        
        return `<img src="https://raw.githubusercontent.com/vvduth/quicksight-01/refs/heads/main/markdown/devops/${imageSrc}" alt="${alt}" style="${IMAGE_STYLE}" />`;
    });
}

/**
 * Process a single markdown file
 */
function processMarkdownFile(filePath) {
    try {
        console.log(`Processing: ${filePath}`);
        
        // Read the file
        const content = fs.readFileSync(filePath, 'utf8');
        
        // Convert images
        const updatedContent = convertMarkdownImagesToHtml(content);
        
        // Check if any changes were made
        if (content !== updatedContent) {
            // Write back to file
            fs.writeFileSync(filePath, updatedContent, 'utf8');
            console.log(`✓ Updated: ${filePath}`);
            return true;
        } else {
            console.log(`- No changes needed: ${filePath}`);
            return false;
        }
    } catch (error) {
        console.error(`✗ Error processing ${filePath}:`, error.message);
        return false;
    }
}

/**
 * Main function
 */
function main() {
    console.log('Starting image conversion...\n');
    
    // Check if markdown folder exists
    if (!fs.existsSync(MARKDOWN_FOLDER)) {
        console.error(`Error: Folder '${MARKDOWN_FOLDER}' does not exist!`);
        process.exit(1);
    }
    
    // Get all markdown files
    const markdownFiles = getAllMarkdownFiles(MARKDOWN_FOLDER);
    
    if (markdownFiles.length === 0) {
        console.log('No markdown files found!');
        return;
    }
    
    console.log(`Found ${markdownFiles.length} markdown file(s)\n`);
    
    // Process each file
    let updatedCount = 0;
    markdownFiles.forEach(file => {
        if (processMarkdownFile(file)) {
            updatedCount++;
        }
    });
    
    console.log(`\n✓ Conversion complete!`);
    console.log(`Total files processed: ${markdownFiles.length}`);
    console.log(`Files updated: ${updatedCount}`);
}

// Run the script
main();