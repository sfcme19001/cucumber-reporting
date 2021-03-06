package net.masterthought.cucumber;

import java.util.ArrayList;
import java.util.List;

import net.masterthought.cucumber.json.Element;
import net.masterthought.cucumber.json.Step;
import net.masterthought.cucumber.util.Status;
import net.masterthought.cucumber.util.Util;

public class TagObject {

    private String tagName;
    private List<ScenarioTag> scenarios = new ArrayList<>();
    private final List<Element> elements = new ArrayList<>();

    public String getTagName() {
        return tagName;
    }

    public String getFileName() {
        // eliminate characters that might be invalid as a file name
        return tagName.replace("@", "").replaceAll(":", "-").trim() + ".html";
    }

    public List<ScenarioTag> getScenarios() {
        return scenarios;
    }

    public void setScenarios(List<ScenarioTag> scenarioTagList) {
        this.scenarios = scenarioTagList;
    }

    public TagObject(String tagName, List<ScenarioTag> scenarios) {
        this.tagName = tagName;
        this.scenarios = scenarios;
    }

    private void populateElements() {
        for (ScenarioTag scenarioTag : scenarios) {
            elements.add(scenarioTag.getScenario());
        }
    }

    public Integer getNumberOfScenarios() {
        List<ScenarioTag> scenarioTagList = new ArrayList<>();
        for (ScenarioTag scenarioTag : this.scenarios) {
            if (!scenarioTag.getScenario().isBackground()) {
                scenarioTagList.add(scenarioTag);
            }
        }
        return scenarioTagList.size();
    }

    public Integer getNumberOfPassingScenarios() {
        return getNumberOfScenariosForStatus(Status.PASSED);
    }

    public Integer getNumberOfFailingScenarios() {
        return getNumberOfScenariosForStatus(Status.FAILED);
    }


    private Integer getNumberOfScenariosForStatus(Status status) {
        List<ScenarioTag> scenarioTagList = new ArrayList<>();
        for (ScenarioTag scenarioTag : this.scenarios) {
            if (!scenarioTag.getScenario().isBackground()) {
                if (scenarioTag.getScenario().getStatus().equals(status)) {
                    scenarioTagList.add(scenarioTag);
                }
            }
        }
        return scenarioTagList.size();
    }

    public String getDurationOfSteps() {
        Long duration = 0L;
        for (ScenarioTag scenarioTag : scenarios) {
            if (scenarioTag.hasSteps()) {
                for (Step step : scenarioTag.getScenario().getSteps()) {
                    duration = duration + step.getDuration();
                }
            }
        }
        return Util.formatDuration(duration);
    }

    public int getNumberOfSteps() {
        int totalSteps = 0;
        for (ScenarioTag scenario : scenarios) {
            if (scenario.hasSteps()) {
                totalSteps += scenario.getScenario().getSteps().length;
            }
        }
        return totalSteps;
    }

    public int getNumberOfStatus(Status status) {
        return Util.findStatusCount(getStatuses(), status);
    }

    /** No-parameters method required for velocity template. */
    public int getNumberOfPasses() {
        return getNumberOfStatus(Status.PASSED);
    }

    /** No-parameters method required for velocity template. */
    public int getNumberOfFailures() {
        return getNumberOfStatus(Status.FAILED);
    }

    /** No-parameters method required for velocity template. */
    public int getNumberOfSkipped() {
        return getNumberOfStatus(Status.SKIPPED);
    }

    /** No-parameters method required for velocity template. */
    public int getNumberOfUndefined() {
        return getNumberOfStatus(Status.UNDEFINED);
    }

    /** No-parameters method required for velocity template. */
    public int getNumberOfMissing() {
        return getNumberOfStatus(Status.MISSING);
    }

    /** No-parameters method required for velocity template. */
    public int getNumberOfPending() {
        return getNumberOfStatus(Status.UNDEFINED);
    }

    private List<Status> getStatuses() {
        List<Status> statuses = new ArrayList<Status>();
        for (ScenarioTag scenarioTag : scenarios) {
            if (scenarioTag.hasSteps()) {
                for (Step step : scenarioTag.getScenario().getSteps()) {
                    statuses.add(step.getStatus());
                }
            }
        }
        return statuses;
    }

    public List<Element> getElements() {
        populateElements();
        return elements;
    }

    public Status getStatus() {
        for (Element element : elements) {
            if (element.getStatus() != Status.PASSED) {
                return Status.FAILED;
            }
        }
        return Status.PASSED;
    }

    public String getRawStatus() {
        return getStatus().toString().toLowerCase();
    }

}
