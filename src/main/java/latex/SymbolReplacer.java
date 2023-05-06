package latex;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Заменяет русские символы на некоторые большие числа, чтобы потом сделать обратную замену,
 * т к библиотека snuggletex заменяет русские буквы на символ "x".
 */
public class SymbolReplacer {
  static private Map<String, String> replaceTable = null;
  /**
   * Start code for replacing.
   */
  static private BigInteger startCode = new BigInteger("6385634785375347534757865");
  static private String russianAlphabhet = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя";

  /**
   *
   */
  private SymbolReplacer(){}

  /**
   * Init table for replace symbols.
   */
  static private void initReplaceTable(){
    replaceTable = new HashMap<>();
    BigInteger one = new BigInteger("1");
    BigInteger symbolCode = startCode;
    for (char sym : russianAlphabhet.toCharArray()) {
      replaceTable.put(new String(Character.toString(sym)), symbolCode.toString());
      symbolCode = symbolCode.add(one);
    }
  }

  /**
   * Replaces all russian symbols to big integers or either.
   * @param input Input string for replace.
   * @param revert If true then replace big integers to russian symbols, else replace russian symbols to big integers
   * @return Result string.
   */
  static public String replaceSymbols(String input, boolean revert){
    if (replaceTable == null)
      initReplaceTable();
    String res = input;
    for (Map.Entry replaceEntry : replaceTable.entrySet()){
      try {
        if (revert)
          res = res.replaceAll((String) replaceEntry.getValue(), (String) replaceEntry.getKey());
        else
          res = res.replaceAll((String) replaceEntry.getKey(), "{" + (String) replaceEntry.getValue() + "}" );
      }
      catch (Exception ex){
        System.out.println("Warning: something wrong with russian symbols");
      }
    }
    return res;
  }

}
