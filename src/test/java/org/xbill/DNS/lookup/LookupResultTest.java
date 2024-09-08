// SPDX-License-Identifier: BSD-2-Clause
package org.xbill.DNS.lookup;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.InetAddress;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;

class LookupResultTest {
  private static final LookupResult PREVIOUS = new LookupResult(false);
  private static final ARecord A_RECORD =
      new ARecord(Name.fromConstantString("a."), DClass.IN, 0, InetAddress.getLoopbackAddress());

  @Test
  void ctor_nullRecords() {
    assertThrows(NullPointerException.class, () -> new LookupResult(null, null));
  }

  @Test
  void ctor_nullAliases() {
    assertThrows(
        NullPointerException.class,
        () -> new LookupResult(PREVIOUS, null, null, false, Collections.emptyList(), null));
  }

  @Test
  void ctor_authOnlyTrue() {
    LookupResult lookupResult = new LookupResult(true);
    assertEquals(true, lookupResult.isAuthenticated());
    assertEquals(0, lookupResult.getAliases().size());
    assertEquals(0, lookupResult.getRecords().size());
    assertEquals(0, lookupResult.getQueryResponsePairs().size());
  }

  @Test
  void ctor_authOnlyFalse() {
    LookupResult lookupResult = new LookupResult(false);
    assertEquals(false, lookupResult.isAuthenticated());
    assertEquals(0, lookupResult.getAliases().size());
    assertEquals(0, lookupResult.getRecords().size());
    assertEquals(0, lookupResult.getQueryResponsePairs().size());
  }

  @Test
  void ctor_singleRecordTrue() {
    LookupResult lookupResult = new LookupResult(A_RECORD, true, A_RECORD);
    assertEquals(true, lookupResult.isAuthenticated());
    assertEquals(0, lookupResult.getAliases().size());
    assertEquals(1, lookupResult.getRecords().size());
    assertEquals(1, lookupResult.getQueryResponsePairs().size());
    assertNull(lookupResult.getQueryResponsePairs().get(A_RECORD));
  }

  @Test
  void ctor_singleRecordFalse() {
    LookupResult lookupResult = new LookupResult(A_RECORD, false, A_RECORD);
    assertEquals(false, lookupResult.isAuthenticated());
    assertEquals(0, lookupResult.getAliases().size());
    assertEquals(1, lookupResult.getRecords().size());
    assertEquals(1, lookupResult.getQueryResponsePairs().size());
    assertNull(lookupResult.getQueryResponsePairs().get(A_RECORD));
  }

  @Test
  void getResult() {
    Record record =
        new ARecord(Name.fromConstantString("a."), DClass.IN, 0, InetAddress.getLoopbackAddress());
    LookupResult lookupResult = new LookupResult(singletonList(record), null);
    assertEquals(singletonList(record), lookupResult.getRecords());
  }

  @Test
  void getAliases() {
    Name name = Name.fromConstantString("b.");
    Record record = new ARecord(name, DClass.IN, 0, InetAddress.getLoopbackAddress());
    LookupResult lookupResult = new LookupResult(singletonList(record), singletonList(name));
    assertEquals(singletonList(name), lookupResult.getAliases());
  }

  @Test
  void isAuthenticatedTrue() {
    LookupResult lookupResult =
        new LookupResult(
            new LookupResult(true),
            null,
            null,
            true,
            singletonList(A_RECORD),
            Collections.emptyList());
    assertEquals(true, lookupResult.isAuthenticated());
  }

  @Test
  void isAuthenticatedFalse() {
    LookupResult lookupResult =
        new LookupResult(
            new LookupResult(false),
            null,
            null,
            false,
            singletonList(A_RECORD),
            Collections.emptyList());
    assertEquals(false, lookupResult.isAuthenticated());
  }

  @Test
  void isAuthenticatedRequiresAllForTrue1() {
    Name nameA = Name.fromConstantString("a.");
    Name nameB = Name.fromConstantString("b.");
    Record cname = new CNAMERecord(nameA, DClass.IN, 0, nameB);
    Record a = new ARecord(nameB, DClass.IN, 0, InetAddress.getLoopbackAddress());
    LookupResult lookupResult1 = new LookupResult(true);
    LookupResult lookupResult2 =
        new LookupResult(lookupResult1, cname, null, true, singletonList(a), singletonList(nameA));
    assertEquals(true, lookupResult2.isAuthenticated());
  }

  @Test
  void isAuthenticatedRequiresAllForTrue2() {
    Name nameA = Name.fromConstantString("a.");
    Name nameB = Name.fromConstantString("b.");
    Record cname = new CNAMERecord(nameA, DClass.IN, 0, nameB);
    Record a = new ARecord(nameB, DClass.IN, 0, InetAddress.getLoopbackAddress());
    LookupResult lookupResult1 = new LookupResult(false);
    LookupResult lookupResult2 =
        new LookupResult(lookupResult1, cname, null, true, singletonList(a), singletonList(nameA));
    assertEquals(false, lookupResult2.isAuthenticated());
  }
}
