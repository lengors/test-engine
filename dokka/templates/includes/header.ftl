<#import "source_set_selector.ftl" as source_set_selector>
<#macro display>
<nav class="navigation" id="navigation-wrapper">
    <div class="navigation--inner">
        <div class="navigation-title">
            <button class="menu-toggle" id="menu-toggle" type="button">toggle menu</button>
            <div class="library-name">
                <@template_cmd name="pathToRoot">
                    <a class="library-name--link" href="${pathToRoot}index.html">
                        <@template_cmd name="projectName">
                            ${projectName}
                        </@template_cmd>
                    </a>
                </@template_cmd>
            </div>
            <div class="library-version">
                <#-- This can be handled by the versioning plugin -->
                <@version/>
            </div>
        </div>
        <@source_set_selector.display/>
    </div>
    <div class="navigation-controls">
        <a class="navigation-controls--btn navigation-controls--theme navigation-controls--github" id="github-toggle-button" role="button" alt="GitHub" href="https://github.com/lengors/test-engine">GitHub</a>
        <a class="navigation-controls--btn navigation-controls--theme navigation-controls--home" id="home-toggle-button" role="button" alt="Home" href="https://lengors.github.io/test-engine">Home</a>
        <a class="navigation-controls--btn navigation-controls--theme" id="theme-toggle-button" role="button">Switch Theme</a>
        <div class="navigation-controls--btn navigation-controls--search" id="searchBar" role="button">search in API</div>
    </div>
</nav>
</#macro>