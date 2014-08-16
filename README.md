AndroidWearCrashReport
======================
A lightweight non intrusive app rate reminder for Android


## Download

### In your wear app
```
dependencies {
    compile 'fr.nicolaspomepuy.androidwearcrashreport:crashreport-wear:0.1'
}
```

### In your mobile app
```
dependencies {
    compile 'fr.nicolaspomepuy.androidwearcrashreport:crashreport-mobile:0.1'
}
```

## Usage

To work, these lines should be added int the onCreate method of your ```Application``` or main ```Activity``` class

### In your mobile app
```
CrashReport.getInstance(this).crashReport.setOnCrashListener(new CrashReport.IOnCrashListener() {
    @Override
    public void onCrashReceived(Throwable throwable) {
        // Manage the crash
    }
});
```

### In your wear app
```
CrashReporter.getInstance(this).start();
```

You can also send caught exceptions

```
CrashReporter.getInstance(this).sendException(yourException);
```

## License

```
 Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
```