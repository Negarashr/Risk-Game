package risk.game.grp.twenty.constant;

/**
 * interface for Constants used in the Game
 */
public interface GameConstant {

  int MIN_NUMBER_OF_COUNTRIES_TO_HAVE_ONE_ARMY = 1;
  int MIN_NUMBER_OF_STRINGS_IN_CONTINENT_LINE = 2;
  int MIN_NUMBER_OF_STRINGS_IN_COUNTRY_LINE = 5;
  int MIN_NUMBER_OF_ARMIES_IN_CALCULATE_ARMIES = 3;
  int FIRST_PLAYER_IN_LIST = 1;
  int NUMBER_OF_CARD_TO_EXCHANGE = 3;

  String PLAYER_PREFIX_DEFAULT_NAME = "Player-";
  String DEFAULT_MAP_PATH = "game_maps/countriesSimpleNew.rmap";
  String SAVE_MAPS_BASE_PATH = "game_maps/saved_game_maps/";
  String DEFAULT_MAPS_BASE_PATH = "game_maps/";
  String DEFAULT_SAVED_GAMES_BASE_PATH = "saved_games/";
  String VALID_MAP_EXTENSION = ".rmap";

}
