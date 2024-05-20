Feature: News browsing

  @show-news
  Scenario: Latest news shown
    Given news update is available
    When he opens the news screen
    Then latest news should be shown

  @handle-errors
  Scenario Outline: News browsing fail
    Given app is unable to show news because off because off "<error type>"
    When user open news screen
    Then news screen should show "<user message>"
    Examples:
      | error type          | user message            |
      | device connectivity | device connection fail  |
      | remote server       | temporarily unavailable |

