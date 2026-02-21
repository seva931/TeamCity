package ui.pages;

import com.codeborne.selenide.ElementsCollection;
import java.util.List;
import java.util.Objects;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.sleep;

public class ProjectsPage extends BasePage <ProjectsPage> {
    @Override
    public String url() {
        return "/favorite/projects?mode=builds";
    }

    private ElementsCollection projects() {
        return $$x("//div[@data-test='subproject']");
    }

    public List<String> visibleProjectIds() {
        sleep(2000);
        return projects().asFixedIterable().stream()
                .map(e -> e.getAttribute("data-project-id"))
                .filter(Objects::nonNull)
                .toList();
    }
}
