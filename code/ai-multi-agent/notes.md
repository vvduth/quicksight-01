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