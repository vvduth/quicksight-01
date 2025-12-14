from datetime import datetime
import json
import os
import sys
from typing import Dict, Any, List, Literal

# Run "uv sync" to install the below packages
from dotenv import load_dotenv
from openai import OpenAI
from pydantic import BaseModel, Field
import requests

import database


load_dotenv()

client = OpenAI()
database.init_db()

class Tool:
    """
    The base class for a tool that can be used by the agent.
    """
    def __init__(self, name: str, description: str, parameters: Dict[str, Any]):
        self.name = name
        self.description = description
        self.parameters = parameters
        
    def get_schema(self) -> Dict[str, Any]:
        """
        return the schema of the tool
        """
        
        return {
            "type": "function",
            "name": self.name,
            "description": self.description,
            "parameters": {
                "type": "object",
                "properties": self.parameters,
                "additionalProperties": False,
                "required": list(self.parameters.keys())
            }
        }
    def execute(self, argument: str) -> str:
        """
        Execute the tool with the given argument.
        This method should be overridden by subclasses.
        """
        raise NotImplementedError("Tool.execute() must be overridden by subclasses.")

class StoreResearchPlanTool(Tool):
    """
    a tool that stores a user research plan into the database
    """
    
    def __init__(self):
        super().__init__(
            name="store_research_plan",
            description="Store a user research plan into the database.",
            parameters={
                "short_summary": {
                    "type": "string",
                    "description": "A brief summary of the research plan."
                },
                "details": {
                    "type": "string",
                    "description": "Detailed information about the research plan."
                }
            }
        )
    def execute(self, arguments: str) -> Dict[str, Any]:
        """
        Execute the tool with the given argument.
        """
        args = json.loads(arguments)
        try:
            result = database.add_research_plan(
                args["short_summary"], args["details"]
            )
            return result
        except Exception as e:
            return {"status": "error", "message": str(e)}

class GetResearchPlansTool(Tool):
    """
    a tool that gets a user's research plans from the database
    """
    
    def __init__(self):
        super().__init__(
            name="get_research_plans",
            description="Get all user research plans from the database.",
            parameters={}
        )
    def execute(self, arguments: str) -> List[Dict[str, Any]]:
        """
        execute the tool with the given argument.
        """
        try:
            result = database.get_research_plans()
            return result
        except Exception as e:
            return {"status": "error", "message": str(e)}

class DeleteResearchPlanTool(Tool):
    """
    a tool that deletes a user research plan from the database
    """
    
    def __init__(self):
        super().__init__(
            name="delete_research_plan",
            description="Delete a user research plan from the database by its ID.",
            parameters={
                "research_plan_id": {
                    "type": "integer",
                    "description": "The ID of the research plan to delete."
                }
            }
        )
    def execute(self, arguments: str) -> Dict[str, Any]:
        """
        execute the tool with the given argument.
        """
        args = json.loads(arguments)
        try:
            database.delete_research_plan(args["research_plan_id"])
            return {"status": "success", "message": f"Research plan {args['research_plan_id']} deleted."}
        except Exception as e:
            return {"status": "error", "message": str(e)}
        

class Agent:
    """
    The base class for an agent that can interact
    with open ai api
    """
    def __init__(self, model: str = "gpt-4o"):
        self.client = client
        self.model = model
        self.messages: list[Dict[str, Any]] = []
        self.tools: Dict[str, Tool] = {}
    def register_tool(self, tool: Tool):
        """
        Register a tool for the agent to use.
        """
        self.tools[tool.name] = tool
    def _get_tool_schemas(self) -> List[Dict[str, Any]]:
        """
        Get the schemas of all registered tools.
        """
        return [tool.get_schema() for tool in self.tools.values()]
    def execute_tool_call(self, tool_call: Any) -> str:
        """
        Execute a tool call and return the result.
        """
        fn_name = tool_call.name
        fn_args = json.loads(tool_call.arguments)
        
        if fn_name in self.tools:
            tool_to_call = self.tools[fn_name]
            try:
                print(f"Executing tool: {fn_name} with args: {fn_args}")
                # the return value of the fucntion is a dict, we need to convert it to string 
                # to be compaltible with the openai api
                return str(tool_to_call.execute(tool_call.arguments))
            except Exception as e:
                return f"Error executing tool {fn_name}: {str(e)}"
        
        return f"Tool {fn_name} not found."
    def run(self):
        """run the agent.
        This method should be overridden by subclasses.
        """
        raise NotImplementedError("Agent.run() must be overridden by subclasses.")
    
class ResearchPlannerAgent(Agent):
    """
    An agent that helps users plan and manage user research activities.
    """
    def __init__(self):
        super().__init__()
        self.register_tool(StoreResearchPlanTool())
        self.register_tool(GetResearchPlansTool())
        self.register_tool(DeleteResearchPlanTool())
    
    def _set_initial_prompt(self):
        """
        Set the initial prompt for the agent.
        """
        self.messages =  [
            {"role": "developer",
               "content": """
                You are a research planner agent. You are tasked with helping the user plan a web research project.
                The user will provide you with a research task and your job is to create a research plan together with the user.
                Your job is NOT to answer the user's question. Instead, you MUST help them build a good research plan that can then be handed off to some other agent to execute.
                The research plan should include things like:
                    - Core topics to be researched
                    - Related topics
                    - Topics that should be avoided
                    - Time frame for web research (i.e, max age of the web search results)
                """},
        ]
    def run(self):
        """run the agent.
        """
        print("Welcome to the Research Planner Agent, please enter your research task.")
        while True:
            user_input = input(
                "Your input (type 'exit' to quit, 'accept' to accept the plan and continue):")
            if user_input.lower() == "exit":
                print("Exiting the Research Planner Agent. Goodbye!")
                sys.exit(0)
            elif user_input.lower() == "accept":
                print("You have accepted the research plan. Proceeding to the next steps...")
                # Here you can add code to hand off the research plan to another agent
                prompt = "Please create a final version of the discussed research plan and return that plan, nothing else, no other comments."
                self.messages.append({"role": "user", "content": prompt})
                response = self.client.responses.create(
                    model=self.model,
                    input=self.messages 
                )
                print("Final Research Plan:")
                print(response.output_text)
                return response.output_text
            # if user inout is not accept or exit, continue the conversation
            self.messages.append({"role": "user", "content": user_input})
            
            while True:
                response = self.client.responses.create(
                    model=self.model,
                    input=self.messages,
                    tools=self._get_tool_schemas(),
                )
                reply = response.output[0]
                self.messages.append(reply)
                
                if reply.type != "function_call":
                    print("Agent:", reply.content)
                    break
                tool_output = self.execute_tool_call(reply)
                
                self.messages.append({
                    "type": "function_call_output",
                    "call_id": reply.call_id,
                    "output": tool_output
                })


class SearchConfig(BaseModel):
    """
    A search configuration.
    """
    search_terms: list[str]
    freshness: Literal["pd", "pw", "pm", "py"] | str = Field(
        ...,
        description="The freshness of the search results. 'pd' = past day, 'pw' = past week, 'pm' = past month, 'py' = past year"
    )

class WebSearchAgent(Agent):
    """
    An agent that performs web searches based on a research plan.
    """
    def __init__(self):
        super().__init__()
        self._set_initial_prompt()
        
    def _set_initial_prompt(self):
        """
        Set the initial prompt for the agent.
        """
        self.messages = [
            {
                "role": "developer",
                "content": f"""
                You are an expert in performing web searches.
                You will be given a research plan and you will need to derive a list of search terms that will be used to perform the search.
                The search terms should be derived from the research plan and should be as specific as possible.
                Focus on deriving impactful search terms that will help the user find the most relevant information.

                Also derive a value for the max age (freshness) of the web search results.
                Today is: {datetime.now().strftime("%Y-%m-%d")}
                """
            }
        ]
    def run(self, research_plan: str):
        """run the agent.
        """
        print("Web Search Agent is processing the research plan, deriving search terms and freshness...")
        self.messages.append({"role": "user", 
                              "content": "Here's the research plan based on which you should derive search terms: " 
                              +  research_plan})
        response = self.client.responses.parse(
            model=self.model,
            input=self.messages,
            text_format=SearchConfig
        )
        search = response.output_parsed
        results = []
        
        for search_term in search.search_terms:
            print(f"Performing web search for term: {search_term} with freshness: {search.freshness}")
            url = "https://api.search.brave.com/res/v1/web/search"
            headers = {
                "Accept": "application/json",
                "X-Subscription-Token": os.getenv("BRAVE_API_KEY"),
            }
            params =  {
                "q": search_term,
                "freshness": search.freshness,
                "count": 10
            }
            
            response = requests.get(url, headers=headers, params=params)
            result = response.json()
            if "web" in result:
                web_results = result["web"]["results"]
                for web_result in web_results:
                    results.append({
                        "search_term": search_term,
                        "url": web_result["url"],
                        "description": web_result["description"],
                    })
            if "news" in result:
                news_results = result["news"]["results"]
                for news_result in news_results:
                    results.append({
                        "search_term": search_term,
                        "url": news_result["url"],
                        "description": news_result["description"],
                    })
            return results
        
class SummaryReportAgent(Agent):
    """
    An agent that generates a summary report based on web search results.
    """
    def __init__(self):
        super().__init__()
        self._set_initial_prompt()
        
    def _set_initial_prompt(self):
        """
        Sets the initial prompt for the agent.
        """
        self.messages = [
            {
                "role": "developer",
                "content": """
                You are a summary report agent.
                You will be given a list of search results (which include short descriptions) and you will need to summarize them into a report.
                The report should be in a format that is easy to understand and use.
                It's important that your report includes those URLs (next to the text they belong to) so that users can dive deeper.
                The report should be in Markdown format. Avoid extra explanations, annotations or text, just return the markdown report.
                """
            }
        ]
    def run(self, search_results: List[Dict[str, Any]]):
        """run the agent.
        """
        print("Summary Report Agent is generating the summary report...")
        self.messages.append({
            "role": "user",
            "content": "Please create a summary (and keep the links!) based on the following search results: " 
            + json.dumps(search_results, indent=2)
        })
        response = self.client.responses.create(
            model=self.model,
            input=self.messages 
        )
        
        report = response.output_text
        print("Summary Report Generated.")
        if report.startswith("```markdown"):
            report = report[11:]
        if report.endswith("```"):
            report = report[:-3]
        return report
    
def main():
    agent = ResearchPlannerAgent()
    research_plan = agent.run()
    search_agent = WebSearchAgent()
    results = search_agent.run(research_plan)
    summary_report_agent = SummaryReportAgent()
    summary_report = summary_report_agent.run(results)
    
    with open("summary_report.md", "w") as f:
        f.write(summary_report)


if __name__ == "__main__":
    main()