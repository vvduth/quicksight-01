## what is ai crew
AiWithCrewai Crew is a multi-agent AI system template powered by [crewAI](https://crewai.com). It enables the setup of collaborative AI agents that work together on complex tasks, maximizing their collective intelligence and capabilities.

## what are crew in this context
* A crew is a group of AI agents designed to collaborate on tasks.

```python
@agent
    def researcher(self) -> Agent:
        return Agent(
            config=self.agents_config['researcher'], # type: ignore[index]
            verbose=True
        )

    @agent
    def reporting_analyst(self) -> Agent:
        return Agent(
            config=self.agents_config['reporting_analyst'], # type: ignore[index]
            verbose=True
        )

```

* how does thes agent register tools? whne we first start i they dont have tools, but they have some config.
* crewai provides a way to define tools and assign them to agents through configuration files or programmatically during agent initialization.
```yaml
researcher:
  role: >
    {topic} Senior Data Researcher
  goal: >
    Uncover cutting-edge developments in {topic}
  backstory: >
    You're a seasoned researcher with a knack for uncovering the latest
    developments in {topic}. Known for your ability to find the most relevant
    information and present it in a clear and concise manner.

reporting_analyst:
  role: >
    {topic} Reporting Analyst
  goal: >
    Create detailed reports based on {topic} data analysis and research findings
  backstory: >
    You're a meticulous analyst with a keen eye for detail. You're known for
    your ability to turn complex data into clear and concise reports, making
    it easy for others to understand and act on the information you provide.
```

### crewai task
* Tasks are what the name kind of implies.

They're the tasks these agents should work on.

So we have a research task which will probably be fulfilled

by the research agent, and a reporting task which will be handled

by the reporting agent. And the idea is that you could

define multiple tasks and also define multiple tasks to a single

agent. Here, we have two tasks for two agents, so one

task per agent though. 