---
title: Gateway by Surevine
---
## Welcome to the Gateway ##

### Overview ###
Although these pages provide some general information about the Gateway,
(for now) the primary purpose is to outline the test strategy and coding
standards that will be adhered to, based on principles within the
[Government Service Design Manual](https://www.gov.uk/service-manual).

### Test Strategy ###
Test effort will be applied at three key levels; unit, integration and a
combination of acceptance and exploratory testing, with the majority of
automated test effort applied during unit and integration testing.

Whilst unit tests are predominantly focused on testing individual units of
source code, integration tests will be written to verify combinations or
interactions between software modules, components and services. Both unit and
integration tests will become assets of the Gateway project, will be
self-documenting, and will be executed regularly (via shared continuous
integration tools) to provide fast and cost-effective feedback.

[Acceptance tests]({{ site.baseurl }}/tests.html) will be written (documented) to
verify high-level functionality end-to-end (e.g. exercising key flows of a use
case). Acceptance tests may be automated (if appropriate) and will be provided
to aid in the delivery of SAT (Site Acceptance Testing).

To accompany unit, integration and acceptance tests, exploratory test
sessions will be conducted to find and test less obvious outcomes. These
sessions will be time-boxed, and where defects are uncovered, either
automated or manual tests will be written to ensure they are not repeated
in subsequent iterations.

### Code Quality ###
Alongside code quality standards, an appropriate code coverage metric will be
established to guide the level of unit and integration testing. These metrics
will be recorded and published as part of the project documentation and tracked
via a continuous integration platform, along with other static analysis reports
and measures to ensure the appropriate level of code quality is met. These
measures will be established on a per project basis and reported on as part of
a fortnightly sprint process.

Our configurations and templates for these measures will be made available upon
request.
