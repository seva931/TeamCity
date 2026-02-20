package ui.pages;

import com.codeborne.selenide.ElementsCollection;

import static com.codeborne.selenide.Selenide.$$;

public class AgentsOverviewPage extends BasePage<AgentsOverviewPage>{
    @Override
    public String url() {
        return "/agents/overwiew";
    }

    private final ElementsCollection agents =
            $$("div[class*='ExpandableAgentPool-module__expandable'] [data-test='agent']");

}
