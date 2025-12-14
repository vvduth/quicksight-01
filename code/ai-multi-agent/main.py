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