Video Streaming Over Twitter
============================

This project encodes binary data into UTF-8 characters to share them over Twitter.
Our goal is to stream video data over tweets. Eventually. Some day.

How does this work?!
====================

Twitter limits their tweets to 140 characters. But one character can contain more than just one byte of information.
They allow UTF-8 characters which may contain up to six byte of information. So we thought of encoding ASCII based texts into UTF-8.

If you just have ASCII characters (one byte long) we can store two ASCII characters in one UTF-8 character (three bytes long).

Original content:
This is a short example text in ASCII! (38/140)

Converted to UTF-8:
周楳⁩猠愠獨潲琠數慭灬攠瑥硴⁩渠䅓䍉䤡 (19/140)

Since we have to add some magical bits for each UTF-8 character we increase the size by one third of the original content.
But since this doesn't count into Twitters character limit we are allowed to store all those information in one single tweet.