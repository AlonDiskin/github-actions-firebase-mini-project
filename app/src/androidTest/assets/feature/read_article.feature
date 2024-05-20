Feature: Read News Article User Journey

  Scenario: User selects an article
    Given user on device home screen
    When he opens app
    And selects article from news screen
    Then user should select device app for article reading
