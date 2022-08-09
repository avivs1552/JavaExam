public class Main {

    private static final int EXPECTED_NUM_OF_ARGUMENTS = 4;
    private static String url;
    private static int numberOfUrls;
    private static int depth;
    private static boolean uniquenessFlag;

    private static void setArguments(String[] args) {
        if (args.length != EXPECTED_NUM_OF_ARGUMENTS) {
            throw new RuntimeException(String.format(
                    "Expected to get %s arguments, but received: %s arguments",
                    EXPECTED_NUM_OF_ARGUMENTS, args.length));
        }
        url = args[0];
        try {
            numberOfUrls = Integer.parseInt(args[1]);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Couldn't fetch number of urls value: %s is invalid value.", args[1]));
        }
        try {
            depth = Integer.parseInt(args[2]);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Couldn't fetch depth value: %s is invalid value.", args[2]));
        }
        try {
            uniquenessFlag = Boolean.parseBoolean(args[3]);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Couldn't fetch uniqueness value: %s is invalid value.", args[3]));
        }
    }

    private static void validateArguments() {
        boolean isUrlValid = UrlUtil.validateUrl(url);
        if (!isUrlValid) {
            throw new RuntimeException(String.format("Url: %s is not valid.", url));
        }
        if (depth < 0) {
            throw new RuntimeException(String.format("Expected a positive depth, but received: %s", depth));
        }

        if (numberOfUrls < 0) {
            throw new RuntimeException(String.format("Expected a natural value, but received: %s", depth));
        }
    }

    public static void main(String[] args) {
        setArguments(args);
        validateArguments();
        UrlUtil.handleUrl(url, numberOfUrls, 0, depth, uniquenessFlag);
        System.out.println("done");
    }
}
