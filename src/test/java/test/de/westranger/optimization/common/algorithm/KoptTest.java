package test.de.westranger.optimization.common.algorithm;

import de.westranger.optimization.common.algorithm.Kopt;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class KoptTest {

  @Test
  public void test2Opt() {
    List<String> lstA = List.of("a", "b");
    List<List<String>> lst = List.of(lstA);

    List<List<List<String>>> expected = new ArrayList<>();
    expected.add(List.of(List.of("a", "b")));
    expected.add(List.of(List.of("b", "a")));

    Kopt<String> kopt = new Kopt<>(lst);
    List<List<List<String>>> result = new ArrayList<>();
    while (kopt.hasNext()) {
      List<List<String>> next = kopt.next();
      result.add(next);
    }
    Assertions.assertEquals(expected, result);
  }

  @Test
  public void test3Opt() {
    List<String> lstA = List.of("a", "b");
    List<String> lstB = List.of("c", "d");
    List<List<String>> lst = List.of(lstB, lstA);

    List<List<List<String>>> expected = new ArrayList<>();
    expected.add(List.of(List.of("c", "d"), List.of("a", "b")));
    expected.add(List.of(List.of("d", "c"), List.of("a", "b")));
    expected.add(List.of(List.of("c", "d"), List.of("b", "a")));
    expected.add(List.of(List.of("d", "c"), List.of("b", "a")));

    expected.add(List.of(List.of("a", "b"), List.of("c", "d")));
    expected.add(List.of(List.of("b", "a"), List.of("c", "d")));
    expected.add(List.of(List.of("a", "b"), List.of("d", "c")));
    expected.add(List.of(List.of("b", "a"), List.of("d", "c")));

    Kopt<String> kopt = new Kopt<>(lst);
    List<List<List<String>>> result = new ArrayList<>();
    while (kopt.hasNext()) {
      List<List<String>> next = kopt.next();
      result.add(next);
    }
    Assertions.assertEquals(expected, result);
  }

  @Test
  public void test4Opt() {
    List<String> lstA = List.of("a", "b");
    List<String> lstB = List.of("c", "d");
    List<String> lstC = List.of("e", "f");
    List<List<String>> lst = List.of(lstC, lstB, lstA);

    List<List<List<String>>> expected = new ArrayList<>();
    expected.add(List.of(List.of("e", "f"), List.of("c", "d"), List.of("a", "b")));
    expected.add(List.of(List.of("f", "e"), List.of("c", "d"), List.of("a", "b")));
    expected.add(List.of(List.of("e", "f"), List.of("d", "c"), List.of("a", "b")));
    expected.add(List.of(List.of("f", "e"), List.of("d", "c"), List.of("a", "b")));

    expected.add(List.of(List.of("e", "f"), List.of("c", "d"), List.of("b", "a")));
    expected.add(List.of(List.of("f", "e"), List.of("c", "d"), List.of("b", "a")));
    expected.add(List.of(List.of("e", "f"), List.of("d", "c"), List.of("b", "a")));
    expected.add(List.of(List.of("f", "e"), List.of("d", "c"), List.of("b", "a")));

    expected.add(List.of(List.of("e", "f"), List.of("a", "b"), List.of("c", "d")));
    expected.add(List.of(List.of("f", "e"), List.of("a", "b"), List.of("c", "d")));
    expected.add(List.of(List.of("e", "f"), List.of("b", "a"), List.of("c", "d")));
    expected.add(List.of(List.of("f", "e"), List.of("b", "a"), List.of("c", "d")));

    expected.add(List.of(List.of("e", "f"), List.of("a", "b"), List.of("d", "c")));
    expected.add(List.of(List.of("f", "e"), List.of("a", "b"), List.of("d", "c")));
    expected.add(List.of(List.of("e", "f"), List.of("b", "a"), List.of("d", "c")));
    expected.add(List.of(List.of("f", "e"), List.of("b", "a"), List.of("d", "c")));

    expected.add(List.of(List.of("c", "d"), List.of("e", "f"), List.of("a", "b")));
    expected.add(List.of(List.of("d", "c"), List.of("e", "f"), List.of("a", "b")));
    expected.add(List.of(List.of("c", "d"), List.of("f", "e"), List.of("a", "b")));
    expected.add(List.of(List.of("d", "c"), List.of("f", "e"), List.of("a", "b")));

    expected.add(List.of(List.of("c", "d"), List.of("e", "f"), List.of("b", "a")));
    expected.add(List.of(List.of("d", "c"), List.of("e", "f"), List.of("b", "a")));
    expected.add(List.of(List.of("c", "d"), List.of("f", "e"), List.of("b", "a")));
    expected.add(List.of(List.of("d", "c"), List.of("f", "e"), List.of("b", "a")));

    expected.add(List.of(List.of("c", "d"), List.of("a", "b"), List.of("e", "f")));
    expected.add(List.of(List.of("d", "c"), List.of("a", "b"), List.of("e", "f")));
    expected.add(List.of(List.of("c", "d"), List.of("b", "a"), List.of("e", "f")));
    expected.add(List.of(List.of("d", "c"), List.of("b", "a"), List.of("e", "f")));

    expected.add(List.of(List.of("c", "d"), List.of("a", "b"), List.of("f", "e")));
    expected.add(List.of(List.of("d", "c"), List.of("a", "b"), List.of("f", "e")));
    expected.add(List.of(List.of("c", "d"), List.of("b", "a"), List.of("f", "e")));
    expected.add(List.of(List.of("d", "c"), List.of("b", "a"), List.of("f", "e")));

    expected.add(List.of(List.of("a", "b"), List.of("e", "f"), List.of("c", "d")));
    expected.add(List.of(List.of("b", "a"), List.of("e", "f"), List.of("c", "d")));
    expected.add(List.of(List.of("a", "b"), List.of("f", "e"), List.of("c", "d")));
    expected.add(List.of(List.of("b", "a"), List.of("f", "e"), List.of("c", "d")));

    expected.add(List.of(List.of("a", "b"), List.of("e", "f"), List.of("d", "c")));
    expected.add(List.of(List.of("b", "a"), List.of("e", "f"), List.of("d", "c")));
    expected.add(List.of(List.of("a", "b"), List.of("f", "e"), List.of("d", "c")));
    expected.add(List.of(List.of("b", "a"), List.of("f", "e"), List.of("d", "c")));

    expected.add(List.of(List.of("a", "b"), List.of("c", "d"), List.of("e", "f")));
    expected.add(List.of(List.of("b", "a"), List.of("c", "d"), List.of("e", "f")));
    expected.add(List.of(List.of("a", "b"), List.of("d", "c"), List.of("e", "f")));
    expected.add(List.of(List.of("b", "a"), List.of("d", "c"), List.of("e", "f")));

    expected.add(List.of(List.of("a", "b"), List.of("c", "d"), List.of("f", "e")));
    expected.add(List.of(List.of("b", "a"), List.of("c", "d"), List.of("f", "e")));
    expected.add(List.of(List.of("a", "b"), List.of("d", "c"), List.of("f", "e")));
    expected.add(List.of(List.of("b", "a"), List.of("d", "c"), List.of("f", "e")));

    Kopt<String> kopt = new Kopt<>(lst);
    List<List<List<String>>> result = new ArrayList<>();
    while (kopt.hasNext()) {
      List<List<String>> next = kopt.next();
      result.add(next);
    }
    Assertions.assertEquals(expected, result);
  }

}
