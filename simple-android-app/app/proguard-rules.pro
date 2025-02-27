# ProGuard rules for the simple Android app
# Add any specific rules for your app below

# Keep the main activity
-keep class com.example.MainActivity { *; }

# Keep all public classes and methods
-keep public class * {
    public protected *;
}

# Add any additional rules as needed for libraries or specific classes