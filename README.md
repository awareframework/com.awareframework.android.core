Copyright (c) 2014 AWARE Mobile Context Instrumentation Middleware/Framework (http://www.awareframework.com)

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

# AWARE Android

This repository contains the the core classes to use while implementing an aware module. 

## Example usage

In your aware module root `build.gradle` file add the jitpack repository.
 
```gradle
repositories {
    // Other repositories..
    
    maven { url "https://jitpack.io" }
}
```

In your library `build.gradle` add the dependency to the core.

```gradle

dependencies {
    implementation 'com.github.awareframework:android-core:master-SNAPSHOT'

    // Other dependencies..
}
```