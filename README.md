#DrumPadMachine

----

TODO:
1) [x] Implement crashlitics for app
2) [ ] Implement Tutorials functionality
3) [ ] Allow users to load custom sounds (23 audio files, pick color and position)
4) [ ] Implement recording songs
5) [ ] Implement settings
      - pan
      - reverb

----

Loading Resources:
1) If has DB -> loads resources from it
2) If no DB -> If has extracted ZIP -> load resources from it
3) If no DB -> If no extracted ZIP -> make API call (get ZIP archive) -> extract ZIP -> load resources from it
4) Use `CoroutineWorker` to save the extracted ZIP in to DB (as it takes 15-20s)
