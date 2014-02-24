if (destination.equals("file:///tmp/transportX")) {
  // Source site
  var filename = "AB";

  // Route
  filename += "CDE";

  // Priority
  filename += "4";

  // File sequence number zero padded to three digits in base 36
  filename += ("00" +(Redis.getIncr("transport1")).toString(36)).slice(-3);

  // File part number
  filename += "01";

  // Data type
  filename += "FG";

  var filename2 = "ABCDF4" +((Redis.getIncr("transport1")).toString(36)).slice(-3) +"01FG";

  // Set the filename for the file copy transfer plugin to pick up
  metadata.put("destinationFilename", (filename +"," +filename2).toUpperCase());

//  java.lang.System.out.println("Using filenames: " +metadata.get("destinationFilename"));
}