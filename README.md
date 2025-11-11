# Java Easy FFmpeg (JEFFmpeg)
An FFmpeg interface that can handle file compression and type conversion.
Because size matters.

## Project Maintainers (4 term 3 members)
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

## Run The Project Locally
Due to issues with creating a working .jar executable,
to use this application you must set it up in an IDE. This is explained further down.

While all steps outlined should lead to running the program easily, feel
free to message @lastered on Discord for any help setting up the project locally.

### Prerequisites:
- Must have [Intellij IDEA](https://www.jetbrains.com/idea/) installed.
- Must have a [Java 21 LTS SDK](https://www.oracle.com/java/technologies/downloads/) installed.
- Must have [FFMPEG](https://ffmpeg.org/) installed, Although this project's 
entire functionality depends on FFMPEG being installed, there are checks in 
place to force the user to install it before the program can be properly used, if you want to see those menus of course.
- Must have a [JavaFX SDK version 21.0.9](https://gluonhq.com/products/javafx/) downloaded somewhere on your device.

Once you have the prerequisites, continue with the setup instructions.

### IDE Setup

Unzip the downloaded JavaFX SDK (**The version you downloaded matters: 21.0.9**) and place it in the `http-hacks-2025-java-project` root folder of this project.

If the unzipped folder does not match the name `javafx-sdk-21`, please rename it accordingly.

Then, go to: File > Project Structure > Libraries, click +, select Java, and select the `lib` folder of the javafx-sdk-21 folder inside the project structure. Press ok, then apply.

In the same project structure screen, select Project and make sure the SDK version is 21 (full version should be 21.0.9). Save and exit out of Project Structure.

Run > Edit Configurations.
Select "Modify Options" and check "Add VM options."
Paste this in the VM options section: `--module-path ./javafx-sdk-21/lib --add-modules javafx.controls,javafx.fxml `.
Also make sure the Launcher class is selected as `FFmpegGUI`

Save and exit. JavaFX should work now.

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