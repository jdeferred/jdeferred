<!--                                                                                                                                  
 Copyright 2013 Ray Tsang
 
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 -->

Fork It
=======
It's recommended that you fork the repository into your own.

File an Issue
=============
Before contributing any code, please [file an issue](https://github.com/jdeferred/jdeferred/issues) first!
The issue ID shall be used as part of the topic branch name that you work on.

Topic Branch
============
Work on a topic branch, with the branch name composed of the Issue ID and something descriptive, such as `11-android-support`

Keeping Your Repo In Sync
=========================

Assuming you have a forked upstream, make sure you've defined the upstream as one of the remotes:

    $ git remote add upstream git://github.com/infinispan/infinispan.git

You should now be able to fetch and pull changes from upstream into your local repository, though you should make sure you have no uncommitted changes:

    $ git fetch upstream
    $ git fetch upstream --tags
    $ git checkout master
    $ git pull upstream master
    $ git push origin master

And for each minor version branches (e.g., 1.2.x):

    $ git checkout 1.2.x
    $ git pull upstream 1.2.x
    $ git push origin 1.2.x

Set ANDROID_HOME Enviroment Variable
====================================
The Android components require Android SDK. Be sure to set ANDROID_HOME:

    $ export ANDROID_HOME=/path/to/android/sdk

