## building a multiagent ai system
* today I buildt a multiagent ai system
* * the project has a main.py file and a database.py file.
* This project here is a very simple

basic web research tool, a tool which

actually does not visit websites and extract content, but

instead a tool that's all about creating a research plan and then

having the AI perform web search based on that

research plan and then return a summary

of the research results, a summary of the links it found in

its web search. 
* while it is not super compelx ap p => it is aan app that take advacnge of multiagent ai systems


### database.py
* i once again used sqlite to build a simple database to store research plans
* every research plan has an id, short_summary, detaileds
* ofc all the CRUD functions are implemented
### OOP appraoch
* I used an OOP approach to build this app
* * tool class:  The base class for a tool that can be used by an agent. It has a name and a description and params for the tool.
  * some tools I will implement later with with th isbae class:
    * StoreResearchPlanTool: a tool to store the research plan in the database
    * GetResearchPlansTool: a tool to get all research plans from the database
    * DeleteResearchPlanTool: a tool to delete a research plan from the database 
* * Agent class: the base class for an agent tthat can interact with open ai api
* alongside thsoe 2, I have some spevcialized agents, which can registoer tools and use them
  * ResearchPlannerAgent:an agent can user the tool to plan a research project, user will provide a topiec and then this agent will go back and forth with user to refine the research plan, it will have access to StoreResearchPlanTool, GetResearchPlansTool, DeleteResearchPlanTool
  * WebSearchAgent: an agent that can perform web search based on a research plan, has no access to any tool => can it be called an agen then? who cares= )=> let move one bro
  * SummaryReportAgent: an agent that can generate a summary report based on the web search results, 

## Brave web search API
* I used Brave web search API to perform web search
* * it is a simple API that takes in a query and returns a list of search results
* we can use it for free with limited quota

## the flow of the whole app
* user will interact with ResearchPlannerAgent to create a research plan
* then user can choose to have WebSearchAgent perform web search based on the research plan
* finally, user can choose to have SummaryReportAgent generate a summary report based on the web search results
```python
def main():
    agent = ResearchPlannerAgent()
    research_plan = agent.run()
    search_agent = WebSearchAgent()
    results = search_agent.run(research_plan)
    summary_report_agent = SummaryReportAgent()
    summary_report = summary_report_agent.run(results)
    
    with open("summary_report.md", "w") as f:
        f.write(summary_report)
```