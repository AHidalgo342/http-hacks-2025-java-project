# Java Easy FFmpeg (JEFFmpeg)
An FFmpeg interface that can handle file compression and type conversion.
Because size matters.
## Project Maintainers
- Szymon Zemojtel
- Marcy Ordinario
- Daryan Worya
- Alex Hidalgo
## Inspiration
FFmpeg is an extremely powerful file processing tool, but it can be 
difficult to use even for familiarized users. To make more common 
and repetitive tasks simpler to use, we made a GUI interface that 
contains functionality for converting file types and compresses to a 
desired size for both video and audio file formats.
## What It Does
This program contains functionality for compressing files and converting
between different file types. It can handle a wide array of several 
video and audio files.
## Built With:
- Java
- JavaFX
- NO generative AI
## Challenges We Ran Into
- Making it in a manner that worked for all operating systems 
  (we wanted the program to work on both Windows and Unix-based systems without having multiple builds) 
- Managing concurrency in JavaFX, a single-threaded application
- Reading and handling FFmpeg output in real time
## Accomplishments That We're Proud Of
- Full Windows and Unix compatibility from a single build.
- Compressing audio and video files as close to the desired file size as 
  consistently possible.
## What We Learned
- Detecting and handling different operating systems in Java.
- More layout and style confidence in JavaFX.
## What's Next For JEFFmpeg
- Image support.
- Video to GIF support.