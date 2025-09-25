# Netflix Data Analytics Dashboard with Amazon QuickSight

A comprehensive data visualization project that transforms Netflix dataset into stunning interactive dashboards using AWS services. This project demonstrates the complete data analytics pipeline from data storage to visualization.

## 🎯 Project Overview

This project takes you from beginner to dashboard complete! You'll learn how to create professional data visualizations by:

- 🪣 **Upload a dataset into an S3 bucket**
- 🆕 **Create an account on Amazon QuickSight**
- 🔗 **Connect your dataset (in the S3 bucket) to Amazon QuickSight**
- 📊 **Create a variety graphs, charts and analysis using QuickSight**
- 🏆 **Publish a dashboard full of insights into your dataset!**

## 📁 Project Structure

```
quicksight-01/
├── netflix_titles.csv    # Netflix dataset (8,000+ titles)
├── manifest.json         # QuickSight data source configuration
├── pdf_charts.pdf       # Exported dashboard (generated after completion)
└── README.md            # This file
```

## 🚀 Getting Started

### Prerequisites

- AWS Account (with appropriate permissions)
- Basic understanding of AWS services
- Dataset files (provided in this repository)

### Step-by-Step Guide

#### Step 1: Prepare Your Dataset 📋

1. **Download the required files:**
   - `netflix_titles.csv` - Contains Netflix movies and TV shows data
   - `manifest.json` - Configuration file for QuickSight data source

#### Step 2: Set Up S3 Storage 🪣

1. **Create an S3 bucket:**
   ```
   Bucket name: nextwork-quicksight-project-[your-name]
   ```

2. **Upload files to S3:**
   - Upload `netflix_titles.csv` to your bucket
   - Update the S3 URL in `manifest.json` file
   - Upload the modified `manifest.json` to your bucket

#### Step 3: Configure Amazon QuickSight 🆕

1. **Sign up for QuickSight:**
   - Navigate to Amazon QuickSight
   - Sign up for Enterprise edition free trial
   - ⚠️ **Important:** Uncheck "Add Paginated Reports" to avoid charges

2. **Configure permissions:**
   - Ensure QuickSight has access to your S3 bucket
   - Verify region consistency between S3 and QuickSight

#### Step 4: Connect Your Data 🔗

1. **Create a new dataset:**
   - Source name: `kaggle-netflix-data`
   - Use the S3 URL of your `manifest.json` file
   - Verify successful connection

#### Step 5: Create Visualizations 📊

Build multiple charts to answer key business questions:

1. **Release Year Distribution** (Donut Chart)
   - Field: `release_year`
   - Type: Donut chart

2. **Content Type by Year** (Horizontal Bar Chart)
   - X-axis: `release_year`
   - Y-axis: `type`
   - Stacked view for percentage analysis

3. **Content Addition Timeline** (Line Chart)
   - Analyze: `date_added`
   - Find peak addition dates

4. **Genre Analysis** (Table/Bar Chart)
   - Focus on: Action & Adventure, TV Comedies, Thrillers
   - Filter by specific genres

5. **Recent Content Analysis** (Filtered Charts)
   - Filter: Content released 2015 or after
   - By genre categories

#### Step 6: Polish and Publish 🏆

1. **Add descriptive titles** to all visualizations
2. **Publish your dashboard** for sharing
3. **Export as PDF** for documentation

#### Step 7: Clean Up 🗑️

**Important:** Delete all resources to avoid charges:
- Terminate QuickSight account
- Delete S3 bucket and all objects

## 📊 Dashboard Features

Your completed dashboard will include:

- **Content Distribution Analysis** - Movies vs TV Shows over time
- **Peak Addition Periods** - When Netflix adds most content
- **Genre Popularity Trends** - Most popular content categories
- **Recent Content Focus** - Analysis of newer additions
- **Interactive Filters** - Dynamic data exploration

## 🎯 Key Learning Outcomes

By completing this project, you'll master:

- **AWS S3** - Cloud storage and data management
- **Amazon QuickSight** - Business intelligence and visualization
- **Data Connection** - Linking cloud storage to analytics tools
- **Dashboard Design** - Creating professional visualizations
- **Data Analysis** - Extracting insights from raw data

## 🔧 Technical Details

### Dataset Information
- **Source**: Netflix Movies and TV Shows dataset
- **Records**: 8,000+ titles
- **Format**: CSV
- **Key Fields**: title, type, release_year, date_added, rating, duration, listed_in

### AWS Services Used
- **Amazon S3** - Data storage
- **Amazon QuickSight** - Data visualization and business intelligence

## 🚨 Important Notes

- **Cost Management**: This project uses AWS free tier services, but always clean up resources
- **Region Consistency**: Ensure S3 bucket and QuickSight are in the same AWS region
- **Permissions**: QuickSight needs proper S3 access permissions
- **Data Refresh**: Learn how to update visualizations with new data

## 🎓 Next Steps

After completing this project:

1. **Explore Advanced Features** - Custom calculations, parameters, and filters
2. **API Integration** - Automate data updates
3. **Share Dashboards** - Learn about QuickSight sharing and permissions
4. **Performance Optimization** - SPICE optimization techniques

## 🤝 Contributing

Feel free to:
- Suggest improvements to visualizations
- Add new analysis questions
- Share alternative chart types
- Contribute to documentation

## 📝 License

This project is for educational purposes. Dataset sourced from publicly available Netflix data.

---

**Ready to build amazing dashboards?** Start with Step 1 and create your first professional data visualization project! 🚀

*Remember: Delete all AWS resources after completion to avoid any charges.*