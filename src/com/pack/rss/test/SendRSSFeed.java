package com.pack.rss.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import com.pack.rss.model.Feed;
import com.pack.rss.model.FeedMessage;
import com.pack.rss.read.RSSFeedParser;

public class SendRSSFeed {

	public static final String feedUrl = "https://cio.economictimes.indiatimes.com/rss/topstories";
	public static final String fromUser= "flowerplus1234@gmail.com";
	public static final String toUser= "bjambu20@gmail.com";
	public static final String password= "********";
	public static final String subject= "RSS Feed";
	public static Connection connection = null;
	public static Statement statement = null;

	public static void main(String[] args) throws SQLException {
		try {
			RSSFeedParser parser = new RSSFeedParser(feedUrl);
			Feed feed = parser.readFeed();
			System.out.println(feed);
			StringBuffer sb = new StringBuffer();
			// get database connection
			connection = DatabaseConnection.getConnection();
			// get the statement
			statement = connection.createStatement();
			for (FeedMessage message : feed.getMessages()) {
				// read the database record with last publish date
				String query = "SELECT title,pubdate,description from RSSFEED order by pubdate desc limit 1";
				ResultSet resultSet = statement.executeQuery(query);
				Optional<ResultSet> optional = Optional.ofNullable(resultSet);
				if (optional.isPresent()) {
					while (resultSet.next()) {
						String pubdate = resultSet.getString("pubdate");
						SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy;HH:mm:ss");
						try {
							Date dbPubDateTime = sf.parse(pubdate);
							System.out.println(dbPubDateTime);
							Date pbDateTime = sf.parse(message.getPubdate());
							// find if new rss feeds received from api
							if (pbDateTime.compareTo(dbPubDateTime) > 0) {
								// save rss feeds to database
								String sql = "INSERT INTO RSSFEED VALUES (" + message.getTitle() + ", "
										+ message.getPubdate() + ", " + message.getDescription() + ")";
								statement.executeUpdate(sql);
								sb.append(message);
							}
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				} else {
					sb.append(message);
				}
			}
			System.out.println(sb.toString());
			String feedsToBeSent = sb.toString();

			// if yes, send feeds to authenticated users.
			// send rss feed over email
			Mailer.send(fromUser, password, toUser, subject, feedsToBeSent); //change if needed
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.close();
			}
			if (statement != null) {
				statement.close();
			}
		}
	}
}