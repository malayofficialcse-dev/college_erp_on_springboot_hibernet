const fs = require('fs');
const path = require('path');

const basePath = "d:\\all\\Major_Project\\sp_boot\\frontend\\src";

const directories = [
    "components\\Shared",
    "components\\Common",
    "pages\\Academic",
    "pages\\Students",
    "pages\\Employees",
    "pages\\Teachers",
    "pages\\Departments",
    "pages\\Attendance",
    "pages\\Finance",
    "pages\\HR",
    "pages\\Library",
    "pages\\Hostel",
    "pages\\Transport",
    "pages\\Events",
    "pages\\Communication",
    "pages\\Reports"
];

directories.forEach(dir => {
    const fullPath = path.join(basePath, dir);
    if (!fs.existsSync(fullPath)) {
        fs.mkdirSync(fullPath, { recursive: true });
        console.log(`✓ Created: ${fullPath}`);
    } else {
        console.log(`- Already exists: ${fullPath}`);
    }
});

console.log("\nAll directories processed successfully!");
