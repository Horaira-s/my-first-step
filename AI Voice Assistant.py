import speech_recognition as sr
import pyttsx3
import datetime
import wikipedia
import webbrowser
import os
import pyjokes

import speech_recognition as sr

# Initialize recognizer class (for recognizing the speech)
r = sr.Recognizer()

# Reading Microphone as source
# listening the speech and store in audio_text variable
with sr.Microphone() as source:
    print("Talk")
    audio_text = r.listen(source)
    print("Time over, thanks")
    # recoginze_() method will throw a request
    # error if the API is unreachable,
    # hence using exception handling
    
    try:
        # using google speech recognition
        print("Text: "+r.recognize_google(audio_text))
    except:
         print("Sorry, I did not get that")
def speak(text):
    print(f"Assistant: {text}")
    try:
        engine = pyttsx3.init()
        engine.say(text)
        engine.runAndWait()
    except:
        print("Speech output not supported in Colab.")
        def wish_user():
    hour = int(datetime.datetime.now().hour)
    if hour < 12:
        speak("Good Morning!")
    elif hour < 18:
        speak("Good Afternoon!")
    else:
        speak("Good Evening!")
    speak("I am your voice assistant. How can I help you today?")
    def take_command():
    return input("You (type your command): ").lower()
def run_assistant():
    wish_user()
    while True:
        query = take_command()

        if 'wikipedia' in query:
            speak("Searching Wikipedia...")
            query = query.replace("wikipedia", "")
            try:
                result = wikipedia.summary(query, sentences=2)
                speak("According to Wikipedia:")
                speak(result)
            except:
                speak("Sorry, I couldn't find anything.")

        elif 'open youtube' in query:
            speak("Opening YouTube...")
            webbrowser.open("https://www.youtube.com/")

        elif 'open google' in query:
            speak("Opening Google...")
            webbrowser.open("https://www.google.com/")

        elif 'time' in query:
            strTime = datetime.datetime.now().strftime("%H:%M:%S")
            speak(f"The current time is {strTime}")

        elif 'joke' in query:
            joke = pyjokes.get_joke()
            speak(joke)

        elif 'exit' in query or 'bye' in query:
            speak("Goodbye! Have a nice day!")
            break

        else:
            speak("Sorry, I didn't understand that. Try again.")
            run_assistant()